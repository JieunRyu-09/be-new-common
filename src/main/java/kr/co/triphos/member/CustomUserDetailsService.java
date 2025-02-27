package kr.co.triphos.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberService memberService;

	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		Member member = memberService.getMemberInfoById(memberId);
		if (member == null) throw new UsernameNotFoundException("잘못된 정보입니다");

		return User.builder()
				.username(member.getMemberId())
				.password(member.getMemberPw())
				.authorities(member.getAuthority())
				.build();
	}
}
