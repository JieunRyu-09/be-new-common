package kr.co.triphos.common.service;


import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.ExcelDataDTO;
import kr.co.triphos.common.dto.ExcelInfoDTO;
import kr.co.triphos.common.entity.ExcelData;
import kr.co.triphos.common.entity.ExcelInfo;
import kr.co.triphos.common.entity.pk.ExcelDataPK;
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
			ExcelInfo excelInfo = new ExcelInfo(excelNm, nowDate, memberId);
			excelInfoRepository.save(excelInfo);
			excelInfoRepository.flush();
			Integer idx = excelInfo.getIdx();
			if (idx == null) throw new RuntimeException("엑셀정보 저장 실패.");

			// ExcelData 작업
			List<ExcelData> excelDataEntityList = new ArrayList<>();
			// excelData 의 정보로 entity 생성 후 saveAll
			// 인덱스를 추적하기 위한 AtomicInteger
			AtomicInteger index = new AtomicInteger(1);  // 인덱스는 1부터 시작
			excelDTO.getExcelDataList().forEach(excelDataItem -> {
				int currentIdx = index.getAndIncrement();
				excelDataEntityList.add(new ExcelData(idx, currentIdx, excelDataItem));
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
			ExcelInfo excelInfo = excelInfoRepository.findByIdx(idx)
					.orElseThrow(() -> new RuntimeException("잘못된 엑셀정보입니다."));
			excelInfo.updateExcelInfoEntity(excelNm, nowDate, memberId);
			excelInfoRepository.save(excelInfo);
			excelInfoRepository.flush();

			// ExcelData 작업
			List<ExcelData> excelDataEntityList = new ArrayList<>();
			// excelData 의 정보로 entity 생성 후 saveAll
			excelDTO.getExcelDataList().forEach(excelDataItem -> {
				excelDataEntityList.add(new ExcelData(idx, excelDataItem.getRowIdx(), excelDataItem));
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

	@Transactional
	public boolean excelDelete(List<Integer> deleteExcelList) throws Exception {
		deleteExcelList.forEach(excelIdx -> {
			excelDataRepository.deleteByPkIdx(excelIdx);
			excelInfoRepository.deleteByIdx(excelIdx);
		});
		return true;
	}

	public List<ExcelInfoDTO> getExcelInfoList (String excelNm, String fromDate, String toDate) throws Exception {
		try {
			List<ExcelInfo> excelInfoEntityList = null;
			if (excelNm == null)  excelInfoEntityList = excelInfoRepository.findByPeriod(fromDate, toDate);
			else				  excelInfoEntityList = excelInfoRepository.findByExcelNmAndPeriod(fromDate, toDate, "%" + excelNm + "%");

			List<ExcelInfoDTO> excelInfoDTOList = new ArrayList<>();
			excelInfoEntityList.forEach(excelInfo -> {
				excelInfoDTOList.add(new ExcelInfoDTO(excelInfo));
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
			List<ExcelData> excelDataEntityList = excelDataRepository.findByPkIdx(idx);

			List<ExcelDataDTO> excelDataDTOList = new ArrayList<>();
			excelDataEntityList.forEach(excelData -> {
				HashMap<String, Object> excelInfoDTO = new HashMap<>();
				excelDataDTOList.add(new ExcelDataDTO(excelData));
			});
			return excelDataDTOList;
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			throw new RuntimeException("엑셀데이터 조회에 실패하였습니다");
		}

	}
}
