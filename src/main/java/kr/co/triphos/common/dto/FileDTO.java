package kr.co.triphos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.triphos.common.entity.ExcelInfo;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Log4j2
public class FileDTO {
	@Id
	@Schema(description = "파일Idx")
	private int 	fileIdx;
	@Schema(description = "파일그룹")
	private String 	fileGroup;
	@Schema(description = "파일유형")
	private String 	fileType;
	@Schema(description = "파일경로")
	private String 	filePath;
	@Schema(description = "변환된 파일명")
	private String 	fileNm;
	@Schema(description = "실제 파일명")
	private String 	realFileNm;
	@Schema(description = "파일사이즈")
	private int 	fileSize;
	@Schema(description = "파일 저장일시")
	private LocalDateTime insDt;
	@Schema(description = "파일 저장자")
	private String 	insMember;
	@Schema(description = "파일삭제 리스트")
	private List<Integer> deleteFileList;
}
