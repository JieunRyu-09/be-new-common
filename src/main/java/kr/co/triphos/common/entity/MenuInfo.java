package kr.co.triphos.common.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kr.co.triphos.common.entity.pk.MenuInfoPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

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
	private LocalDateTime insDt;
	private String 	insMember;
	private LocalDateTime	updDt;
	private String	updMember;
}
