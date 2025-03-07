package kr.co.triphos.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
	private String 	memberId;
	private String 	memberNm;
	private String 	memberPw;
	private String 	newMemberPw;
	private String 	email;
	private String 	phone;
	private String 	memberType;
	private String 	memberStatus;
	private String 	deviceType;
	private String 	deviceToken;
	private String 	delYn;
	private String 	loginCnt;
	private String 	loginDt;
	private String 	lastPwd_change_dt;
	private String 	authKey;
	private String 	accessIp;
	private String 	accessBrowser;
	private String 	adminYn;
	private String 	insDt;
	private String 	insMember;
	private String 	updDt;
	private String 	updMember;
	private String 	authority;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	public CustomUserDetails(Member member) {
		try {
			Field[] dtoFields = member.getClass().getDeclaredFields(); // MemberEntity의 모든 필드 가져오기
			Field[] entityFields = this.getClass().getDeclaredFields(); // CustomUserDetails의 모든 필드 가져오기

			for (Field dtoField : dtoFields) {
				dtoField.setAccessible(true);  // DTO 필드 접근
				if ((dtoField.getModifiers() & (STATIC | FINAL)) != 0) continue;

				for (Field entityField : entityFields) {
					entityField.setAccessible(true);  // Entity 필드 접근
					if ((entityField.getModifiers() & (STATIC | FINAL)) != 0) continue;

					// memberEntity 필드명과 CustomUserDetails 필드명이 같으면 값을 할당
					if (dtoField.getName().equals(entityField.getName())) {
						Object value = dtoField.get(member); // DTO의 값 가져오기
						entityField.set(this, value); // Entity 필드에 값 설정
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(authority));
	}

	@Override
	public String getPassword() {
		return memberPw;
	}

	@Override
	public String getUsername() {
		return memberNm;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
