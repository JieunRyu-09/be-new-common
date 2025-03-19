package kr.co.triphos.member.entity;

import kr.co.triphos.member.entity.pk.MenuInfoPK;
import kr.co.triphos.member.entity.pk.MenuMemberAuthPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "sy_menu_info")
public class MenuInfo {
	@EmbeddedId
	private MenuInfoPK pk;

	private int 	orderBy;
	private String	menuId;
	private String	menuTitle;
	private String 	menuLink;
	private String	menuLinkDev;
	private String	displayYn;
	private String	note;
	private String	insDt;
	private String 	insMember;
	private String	updDt;
	private String	updMember;
}
