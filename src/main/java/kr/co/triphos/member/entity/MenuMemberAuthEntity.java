package kr.co.triphos.member.entity;

import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.entity.pk.MenuMemberAuthEntityPK;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "sy_menu_member_auth")
public class MenuMemberAuthEntity {
	@EmbeddedId
	private MenuMemberAuthEntityPK pk;

	private String	useYn;
	private String	authSearch;
	private String	authIns;
	private String	authDel;
	private String	authMod;
	private String	excelExport;
	private String 	note;
	private LocalDateTime 	insDt;
	private String	insMember;
	private LocalDateTime updDt;
	private String	updMember;

	public void updateMenuMemberAuth(MenuMemberAuthDTO menuMemberAuthDTO) {
		try {
			Field[] dtoFields = menuMemberAuthDTO.getClass().getDeclaredFields(); // DTO의 모든 필드 가져오기
			Field[] entityFields = this.getClass().getDeclaredFields(); // Entity의 모든 필드 가져오기

			for (Field dtoField : dtoFields) {
				dtoField.setAccessible(true);  // DTO 필드 접근
				if ((dtoField.getModifiers() & (STATIC | FINAL)) != 0) continue;

				for (Field entityField : entityFields) {
					entityField.setAccessible(true);  // Entity 필드 접근
					if ((entityField.getModifiers() & (STATIC | FINAL)) != 0) continue;

					// DTO 필드명과 Entity 필드명이 같으면 값을 할당
					if (dtoField.getName().equals(entityField.getName())) {
						Object value = dtoField.get(menuMemberAuthDTO); // DTO의 값 가져오기
						if (value != null && value != "") {
							entityField.set(this, value); // Entity 필드에 값 설정
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
