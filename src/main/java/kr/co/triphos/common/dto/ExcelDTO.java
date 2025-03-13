package kr.co.triphos.common.dto;

import kr.co.triphos.common.entity.ExcelDataEntity;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Data
@Log4j2
public class ExcelDTO {
	private Integer idx;
	private String 	excelNm;
}
