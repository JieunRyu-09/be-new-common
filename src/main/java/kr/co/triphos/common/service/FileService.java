package kr.co.triphos.common.service;


import jakarta.transaction.Transactional;
import kr.co.triphos.common.entity.FileInfo;
import kr.co.triphos.common.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileService {
	@Value("${file.upload-dir}")
	private String rootPath;

	private final FileInfoRepository fileInfoRepository;

	@Transactional
	public boolean fileSave (List<MultipartFile> fileList, String memberId) throws Exception {
		// 파일경로 선언
		LocalDateTime nowDate 	= LocalDateTime.now();
		String yearMonth 		= nowDate.format(DateTimeFormatter.ofPattern("yyyyMM"));

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
				String shortPath = "/" + yearMonth + "/";
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

	public List<HashMap<String, Object>> getFileList (String fileNm, String fromDate, String toDate) {
		// 반환할 파일목록
		List<FileInfo> fileInfoEntityList = new ArrayList<>();
		// 파일명 입력에 따라 조회
		if (fileNm == null) fileInfoEntityList = fileInfoRepository.findByPeriod(fromDate, toDate);
		else 				fileInfoEntityList = fileInfoRepository.findByRealFileNmAndPeriod(fromDate, toDate, "%" + fileNm + "%");

		List<HashMap<String, Object>> fileInfoDTOList = new ArrayList<>();
		fileInfoEntityList.forEach(fileInfoEntity -> {
			HashMap<String, Object> fileInfoDTO = new HashMap<>();
			fileInfoDTO.put("fileIdx", 		fileInfoEntity.getFileIdx());
			fileInfoDTO.put("fileNm", 		fileInfoEntity.getRealFileNm());
			fileInfoDTO.put("fileSize", 	fileInfoEntity.getFileSize());
			fileInfoDTO.put("insDt", 		fileInfoEntity.getInsDt());
			fileInfoDTO.put("insMember",	fileInfoEntity.getInsMember());
			fileInfoDTOList.add(fileInfoDTO);
		});
		return fileInfoDTOList;
	}

	public HashMap<String, Object> downloadFile (Integer fileIdx) throws Exception {
		HashMap<String, Object> fileData = new HashMap<>();
		FileInfo fileInfo = fileInfoRepository.findByFileIdx(fileIdx).orElseThrow(() -> new RuntimeException("잘못된 파일정보입니다"));

		String realFileNm 	= fileInfo.getRealFileNm();
		String filePath		= fileInfo.getFilePath();
		String fileName		= fileInfo.getFileNm();
		String encodedFileName = URLEncoder.encode(realFileNm, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

		File file = new File(filePath, fileName);
		if (!file.exists() || !file.isFile()) {
			throw new RuntimeException("잘못된 파일정보입니다. 파일을 업로드한 환경을 확인하여주십시오.");
		}
		InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
		long fileSize = file.length();
		if (fileSize == 0) {
			throw new RuntimeException("파일용량이 없습니다. 파일을 업로드한 환경을 확인하여주십시오.");
		}

		fileData.put("encodedFileName", encodedFileName);
		fileData.put("resource", resource);
		fileData.put("fileSize", fileSize);
		return fileData;
	}

	@Transactional
	public void deleteFile (List<Integer> deleteFileList) throws Exception {
		deleteFileList.forEach(fileIdx -> {
			FileInfo fileInfo = fileInfoRepository.findByFileIdx(fileIdx).orElseThrow(() ->
					new RuntimeException("삭제할 파일정보를 찾지 못하였습니다.")
			);

			String deleteFilePath = fileInfo.getFilePath();
			String deleteFileName = fileInfo.getFileNm();
			File file = new File(deleteFilePath, deleteFileName);
			boolean deleteResult = file.delete();
			if (!deleteResult) {
				throw new RuntimeException("파일삭제 중 오류가 발생하였습니다.");
			}
			fileInfoRepository.deleteByFileIdx(fileIdx);
		});
	}
}
