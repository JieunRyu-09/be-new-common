package kr.co.triphos.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Log4j2
@SuperBuilder
public class MenuMemberAuthDTO {
	@NotBlank(message = "메뉴 Id를 입력하세요.")
	@Schema(description = "메뉴 ID")
	private String 	menuId;

	@NotBlank(message = "사용자 Id를 입력하세요.")
	@Schema(description = "사용자 ID")
	private String 	memberId;

	@NotBlank(message = "사용여부를 입력하세요")
	@Schema(description = "사용여부 (Y/N)")
	private String 	useYn;

	@NotBlank(message = "검색권한을 입력하세요.")
	@Schema(description = "검색권한 (Y/N)")
	private String 	authSearch;

	@NotBlank(message = "생성권한을 입력하세요")
	@Schema(description = "생성권한 (Y/N)")
	private String 	authIns;

	@NotBlank(message = "삭제권한을 입력하세요")
	@Schema(description = "삭제권한 (Y/N)")
	private String 	authDel;

	@NotBlank(message = "수정권한을 입력하세요.")
	@Schema(description = "수정권한 (Y/N)")
	private String 	authMod;

	@NotBlank(message = "엑셀권한을 입력하세요")
	@Schema(description = "엑셀권한 (Y/N)")
	private String 	excelExport;
}
