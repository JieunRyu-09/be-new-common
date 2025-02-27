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
public class MemberService {

	private final MemberRepository memberRepository;

	public Member getMemberInfoById(String memberId) {
		return memberRepository.findByMemberId(memberId);
	}

	public Member getMemberInfoByIdAndPw(String memberId, String memberPw) {
		return memberRepository.findByMemberIdAndMemberPw(memberId, memberPw);
	}

	public Member createMember(Member member) {
		return memberRepository.save(member);
	}

}
