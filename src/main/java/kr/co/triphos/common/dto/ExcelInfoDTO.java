package kr.co.triphos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Log4j2
public class ExcelInfoDTO {
	@Schema(description = "엑셀파일 idx")
	private Integer idx;
	@Schema(description = "엑셀파일 이름")
	private String 	excelNm;
	@Schema(description = "저장일자")
	private LocalDateTime insDt;
	@Schema(description = "저장자")
	private String 	insMember;
	@Schema(description = "수정자")
	private LocalDateTime updDt;
	@Schema(description = "수정일자")
	private String 	updMember;

	public ExcelInfoDTO (ExcelInfoEntity excelInfoEntity) {
		this.idx 		= excelInfoEntity.getIdx();
		this.excelNm 	= excelInfoEntity.getExcelNm();
		this.insDt 		= excelInfoEntity.getInsDt();
		this.insMember 	= excelInfoEntity.getInsMember();
		this.updDt 		= excelInfoEntity.getUpdDt();
		this.updMember 	= excelInfoEntity.getUpdMember();
	}
}
