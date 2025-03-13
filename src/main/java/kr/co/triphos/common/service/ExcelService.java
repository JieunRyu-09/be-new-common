package kr.co.triphos.common.service;


import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExcelService {

	private boolean excelSave(ExcelDTO excelDTO, String memberId) throws Exception {
		try {
			//ExcelInfoEntity excelInfoEntity = ExcelDTO.get

			return true;
		}
		catch (RuntimeException ex) {

			return false;
		}
	}
}
