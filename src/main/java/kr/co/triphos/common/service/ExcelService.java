package kr.co.triphos.common.service;


import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.ExcelDataDTO;
import kr.co.triphos.common.dto.ExcelInfoDTO;
import kr.co.triphos.common.entity.ExcelDataEntity;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import kr.co.triphos.common.entity.pk.ExcelDataEntityPK;
import kr.co.triphos.common.repository.ExcelDataRepository;
import kr.co.triphos.common.repository.ExcelInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExcelService {
	private final ExcelInfoRepository excelInfoRepository;
	private final ExcelDataRepository excelDataRepository;

	@Transactional
	public boolean excelSave(ExcelDTO excelDTO, String memberId) throws Exception {
		try {
			String excelNm = excelDTO.getExcelNm();
			LocalDateTime nowDate = LocalDateTime.now();

			// ExcelInfo 작업
			ExcelInfoEntity excelInfoEntity = new ExcelInfoEntity(excelNm, nowDate, memberId);
			excelInfoRepository.save(excelInfoEntity);
			excelInfoRepository.flush();
			Integer idx = excelInfoEntity.getIdx();
			if (idx == null) throw new RuntimeException("엑셀정보 저장 실패.");

			// ExcelData 작업
			List<ExcelDataEntity> excelDataEntityList = new ArrayList<>();
			// excelData 의 정보로 entity 생성 후 saveAll
			// 인덱스를 추적하기 위한 AtomicInteger
			AtomicInteger index = new AtomicInteger(1);  // 인덱스는 1부터 시작
			excelDTO.getExcelDataList().forEach(excelDataItem -> {
				int currentIdx = index.getAndIncrement();
				excelDataEntityList.add(new ExcelDataEntity(idx, currentIdx, excelDataItem));
			});
			excelDataRepository.saveAll(excelDataEntityList);
			return true;
		}
		catch (RuntimeException ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	@Transactional
	public boolean excelUpdate(ExcelDTO excelDTO, String memberId) throws Exception {
		try {
			Integer idx	= excelDTO.getIdx();
			String excelNm = excelDTO.getExcelNm();
			LocalDateTime nowDate = LocalDateTime.now();

			// ExcelInfo 작업
			// idx로 조회해서 있는 정보인지 체크
			ExcelInfoEntity excelInfoEntity = excelInfoRepository.findByIdx(idx)
					.orElseThrow(() -> new RuntimeException("잘못된 엑셀정보입니다."));
			excelInfoEntity.updateExcelInfoEntity(excelNm, nowDate, memberId);
			excelInfoRepository.save(excelInfoEntity);
			excelInfoRepository.flush();

			// ExcelData 작업
			List<ExcelDataEntity> excelDataEntityList = new ArrayList<>();
			// excelData 의 정보로 entity 생성 후 saveAll
			excelDTO.getExcelDataList().forEach(excelDataItem -> {
				excelDataEntityList.add(new ExcelDataEntity(idx, excelDataItem.getRowIdx(), excelDataItem));
			});
			excelDataRepository.saveAll(excelDataEntityList);

			excelDTO.getDeleteDataList().forEach(deleteData -> {
				excelDataRepository.deleteByIdxAndRowIdx(idx, deleteData);
			});

			return true;
		}
		catch (RuntimeException ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	public List<ExcelInfoDTO> getExcelInfoList (String excelNm) throws Exception {
		try {
			List<ExcelInfoEntity> excelInfoEntityList = null;
			if (excelNm == null)  excelInfoEntityList = excelInfoRepository.findAll();
			else				  excelInfoEntityList = excelInfoRepository.findByExcelNmLike("%" + excelNm + "%");

			List<ExcelInfoDTO> excelInfoDTOList = new ArrayList<>();
			excelInfoEntityList.forEach(excelInfoEntity -> {
				excelInfoDTOList.add(new ExcelInfoDTO(excelInfoEntity));
			});
			return excelInfoDTOList;
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			throw new RuntimeException("엑셀정보 목록조회에 실패하였습니다");
		}

	}

	public List<ExcelDataDTO> getExcelDataList (int idx) throws Exception {
		try {
			List<ExcelDataEntity> excelDataEntityList = excelDataRepository.findByPkIdx(idx);

			List<ExcelDataDTO> excelDataDTOList = new ArrayList<>();
			excelDataEntityList.forEach(excelDataEntity -> {
				HashMap<String, Object> excelInfoDTO = new HashMap<>();
				excelDataDTOList.add(new ExcelDataDTO(excelDataEntity));
			});
			return excelDataDTOList;
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			throw new RuntimeException("엑셀데이터 조회에 실패하였습니다");
		}

	}
}
