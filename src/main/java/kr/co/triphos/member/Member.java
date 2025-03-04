package kr.co.triphos.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import static java.lang.reflect.Modifier.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "member")
public class Member {
	@Id
	private String 	memberId;
	private String 	memberPw;
	private String 	authority;
	private String 	memberNm;
	private String 	memberEmail;
	private String 	memberPhone;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

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
						entityField.set(this, value); // Entity 필드에 값 설정
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void updateMember(MemberDTO memberDTO) {
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
						entityField.set(this, value); // Entity 필드에 값 설정
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
