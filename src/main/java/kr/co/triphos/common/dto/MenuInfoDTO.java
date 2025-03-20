package kr.co.triphos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.triphos.common.entity.MenuInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Data
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
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

	public MenuInfoDTO (MenuInfo menuInfo) {
		this.mainCd			= menuInfo.getPk().getMainCd();
		this.sub1Cd			= menuInfo.getPk().getSub1Cd();
		this.sub2Cd			= menuInfo.getPk().getSub2Cd();
		this.orderBy		= menuInfo.getOrderBy();
		this.menuId			= menuInfo.getMenuId();
		this.menuTitle		= menuInfo.getMenuTitle();
		this.menuLink		= menuInfo.getMenuLink();
		this.menuLinkDev	= menuInfo.getMenuLinkDev();
		this.displayYn		= menuInfo.getDisplayYn();
	}
}
