package kr.co.triphos.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
	private String 	memberId;
	private String 	memberPw;
	private String 	memberNm;
	private String 	memberEmail;
	private String 	memberPhone;
	private String 	authority;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	public CustomUserDetails(Member member) {
		this.memberId 				= member.getMemberId();
		this.memberPw 				= member.getMemberPw();
		this.memberNm 				= member.getMemberNm();
		this.memberEmail 			= member.getMemberEmail();
		this.memberPhone 			= member.getMemberPhone();
		this.authority 				= member.getAuthority();
		this.accountNonExpired 		= member.isAccountNonExpired();
		this.accountNonLocked 		= member.isAccountNonLocked();
		this.credentialsNonExpired 	= member.isCredentialsNonExpired();
		this.enabled 				= member.isEnabled();
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
