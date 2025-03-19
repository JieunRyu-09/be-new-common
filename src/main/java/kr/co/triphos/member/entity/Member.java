package kr.co.triphos.member.entity;

import kr.co.triphos.member.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

@Data
@Entity(name = "Member")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "mm_member_info")
public class Member {
	@Id
	private String 	memberId;

	@NotNull
	private String 	memberNm;
	@NotNull
	private String 	memberPw;
	@NotNull
	@Email
	private String 	email;
	@NotNull
	private String 	phone;
	@NotNull
	private String 	memberType;
	@NotNull
	private String 	memberStatus;
	private String 	deviceType;
	private String 	deviceToken;
	@NotNull
	private String 	delYn;
	private String 	loginCnt;
	private String 	loginDt;
	private String 	lastPwdChangeDt;
	private String 	authKey;
	private String 	accessIp;
	private String 	accessBrowser;
	@NotNull
	private String 	adminYn;
	@Column(insertable = false, updatable = false)
	private String 	insDt;
	@NotNull
	private String 	insMember;
	private String 	updDt;
	private String 	updMember;
	@Builder.Default
	private boolean accountNonExpired = true;
	@Builder.Default
	private boolean accountNonLocked = true;
	@Builder.Default
	private boolean credentialsNonExpired = true;
	@Builder.Default
	private boolean enabled = true;

	public Member(MemberDTO memberDTO) {
		try {
			Field[] dtoFields = memberDTO.getClass().getDeclaredFields(); // DTO의 모든 필드 가져오기
			Field[] entityFields = this.getClass().getDeclaredFields(); // Entity의 모든 필드 가져오기

			for (Field dtoField : dtoFields) {
				dtoField.setAccessible(true);  // DTO 필드 접근
				if ((dtoField.getModifiers() & (STATIC | FINAL)) != 0) continue;

				for (Field entityField : entityFields) {
					entityField.setAccessible(true);  // Entity 필드 접근
					if ((entityField.getModifiers() & (STATIC | FINAL)) != 0) continue;

					// DTO 필드명과 Entity 필드명이 같으면 값을 할당
					if (dtoField.getName().equals(entityField.getName())) {
						Object value = dtoField.get(memberDTO); // DTO의 값 가져오기
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

	public void updateMember(Object memberDTO) {
		try {
			Field[] dtoFields = memberDTO.getClass().getDeclaredFields(); // DTO의 모든 필드 가져오기
			Field[] entityFields = this.getClass().getDeclaredFields(); // Entity의 모든 필드 가져오기

			for (Field dtoField : dtoFields) {
				dtoField.setAccessible(true);  // DTO 필드 접근
				if ((dtoField.getModifiers() & (STATIC | FINAL)) != 0) continue;

				for (Field entityField : entityFields) {
					entityField.setAccessible(true);  // Entity 필드 접근
					if ((entityField.getModifiers() & (STATIC | FINAL)) != 0) continue;

					// DTO 필드명과 Entity 필드명이 같으면 값을 할당
					if (dtoField.getName().equals(entityField.getName())) {
						Object value = dtoField.get(memberDTO); // DTO의 값 가져오기
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
