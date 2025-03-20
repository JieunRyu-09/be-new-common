package kr.co.triphos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Data
@Log4j2
public class MenuInfoDTO {
	@Schema(description = "엑셀파일 idx")
	private int 	mainCd;
	@Schema(description = "엑셀파일 idx")
	private int 	sub1Cd;
	@Schema(description = "엑셀파일 idx")
	private int 	sub2Cd;
	@Schema(description = "엑셀파일 idx")
	private int 	orderBy;
	@Schema(description = "엑셀파일 idx")
	private String 	menuId;
	@Schema(description = "엑셀파일 idx")
	private String	menuTitle;
	@Schema(description = "엑셀파일 idx")
	private String	menuLink;
	@Schema(description = "엑셀파일 idx")
	private String	menuLinkDev;
	@Schema(description = "엑셀파일 idx")
	private String	displayYn;
}
