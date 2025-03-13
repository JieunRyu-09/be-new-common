package kr.co.triphos.common.service;


import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import kr.co.triphos.common.entity.pk.ExcelDataEntityPK;
import kr.co.triphos.common.repository.ExcelInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExcelService {
	private final ExcelInfoRepository excelInfoRepository;

	@Transactional
	public boolean excelSave(ExcelDTO excelDTO, String memberId) throws Exception {
		try {
			int idx	= excelDTO.getIdx();
			String excelNm = excelDTO.getExcelNm();
			LocalDateTime nowDate = LocalDateTime.now();

			// ExcelInfo 작업
			// idx로 조회해서 있는 정보인지 체크
			ExcelInfoEntity excelInfoEntity = excelInfoRepository.findByIdx(idx).orElse(new ExcelInfoEntity());
			// 기존에 없는 정보라면 신규정보로 생성
			if (idx == 0) {
				excelInfoEntity.createExcelInfoEntity(excelNm, nowDate, memberId);
			}
			// 기존에 있는 정보라면 update 정보 추가
			else {
				excelInfoEntity.updateExcelInfoEntity(excelNm, nowDate, memberId);
			}
			excelInfoRepository.save(excelInfoEntity);

			// ExcelData 작업
			excelDTO.getExcelDataList().forEach(excelDataItem -> {
				//int orderBy = excelDataItem.
				//ExcelDataEntityPK pk = new ExcelDataEntityPK(idx, d)
			});

			return true;
		}
		catch (RuntimeException ex) {

			return false;
		}
	}
}
