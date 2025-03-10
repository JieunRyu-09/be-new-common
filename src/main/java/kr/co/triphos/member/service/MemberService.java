package kr.co.triphos.member.service;

import kr.co.triphos.member.dto.memberDTO.MemberUpdateDTO;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.dto.memberDTO.MemberDTO;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	public Member getMemberInfoById(String memberId) {
		return memberRepository.findByMemberId(memberId).orElse(null);
	}

	public Member getMemberInfoByIdAndPw(String memberId, String memberPw) {
		return memberRepository.findByMemberIdAndMemberPw(memberId, memberPw);
	}

	@Transactional
	public boolean createMember(MemberDTO memberDTO) throws Exception{
		String memberId = memberDTO.getMemberId();
		String memberPw = memberDTO.getMemberPw();
		Member existMember = memberRepository.findByMemberId(memberId).orElse(new Member());

		if (existMember.getMemberId() != null) {throw new Exception("이미 등록된 사용자입니다.");}

		Member newMember = new Member(memberDTO);
		newMember.setMemberPw(passwordEncoder.encode(memberDTO.getMemberPw()));
		memberRepository.save(newMember);
		return true;
	}

	@Transactional
	public boolean updateMemberPw(MemberUpdateDTO memberDTO) throws Exception{
		String memberId 	= memberDTO.getMemberId();
		String memberPw 	= memberDTO.getMemberPw();
		String newMemberPw 	= memberDTO.getNewMemberPw();
		Member existMember 	= memberRepository.findByMemberId(memberId).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));
		//if (!passwordEncoder.matches(memberPw, existMember.getMemberPw())) {throw new Exception("잘못된 기존 비밀번호입니다.");}

		try {
			// 사용자가 입력한 정보만 업데이트
			existMember.updateMember(memberDTO);
			// 비밀번호 업데이트
			if (newMemberPw != null) {
				newMemberPw = passwordEncoder.encode(newMemberPw);
				existMember.setMemberPw(newMemberPw);;
			}
			memberRepository.save(existMember);
			return true;
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}
}
