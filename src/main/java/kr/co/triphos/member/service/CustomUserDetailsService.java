package kr.co.triphos.member.service;

import kr.co.triphos.member.entity.MemberEntity;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		MemberEntity memberEntity = memberRepository.findByMemberId(memberId)
				.orElseThrow(() -> new UsernameNotFoundException("잘못된 회원정보입니다."));

		return new org.springframework.security.core.userdetails.User(
				memberEntity.getMemberId(),
				memberEntity.getMemberPw(),
				Collections.emptyList() // 권한 추가 가능
		);
	}
}
