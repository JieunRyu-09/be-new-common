package kr.co.triphos.common.service;


import kr.co.triphos.common.entity.FileInfo;
import kr.co.triphos.common.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileService {

	private final FileInfoRepository fileInfoRepository;

	@Transactional
	public boolean fileSave (List<MultipartFile> fileList, String memberId) throws Exception {
		try {
			// 파일경로 선언
			LocalDateTime nowDate 	= LocalDateTime.now();
			String yearMonth 		= nowDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
			String rootPath 	= System.getProperty("user.dir");

			// 파일 개별 저장
			String fileGroup = "FGI001";
			String fileType  = "FTI001";
			fileList.forEach(fileItem -> {
				try {
					long fileSize = fileItem.getSize();
					String realFileNm = fileItem.getOriginalFilename();
					if (realFileNm == null) throw new RuntimeException("잘못된 파일정보입니다.");

					// FileInfo Entity 생성
					FileInfo fileInfo = FileInfo.builder()
							.fileGroup(fileGroup)
							.fileType(fileType)
							.filePath("temp")
							.fileNm("temp")
							.realFileNm(realFileNm)
							.fileSize(fileSize)
							.insDt(nowDate)
							.insMember(memberId)
							.build();
					// FileInfo Entity 저장 및 저장하면서 생긴 IDX 조회
					// 조회한 IDX로 추후 파일명 등 설정
					fileInfoRepository.save(fileInfo);
					fileInfoRepository.flush();
					Integer idx = fileInfo.getFileIdx();
					if (idx == null) throw new RuntimeException("파일 저장 실패.");

					// 확장자, 파일경로 등 파일정보 설정
					// 경로 체크 후 없으면 경로까지 생성
					String fileExtender = realFileNm.substring(realFileNm.lastIndexOf("."));
					String fileName = yearMonth + "_" + idx + fileExtender;
					String shortPath = "/Files/" + yearMonth + "/";
					Path filePath = Paths.get(rootPath + shortPath + fileName);
					Path existPath = Paths.get(rootPath + shortPath);
					if (!Files.exists(existPath)) Files.createDirectories(existPath);
					// 파일 저장(출력)
					fileItem.transferTo(filePath.toFile());
					// 파일 경로 및 파일명 정보 저장
					fileInfo.setFileNm(fileName);
					fileInfo.setFilePath(existPath.toString());
					fileInfoRepository.save(fileInfo);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			return true;
		}
		catch (RuntimeException ex) {
			log.error(ex.getMessage());
			throw new RuntimeException("파일 저장에 실패하였습니다.");
		}
		catch (Exception ex) {
			throw new Exception(ex);
		}
	}
}
