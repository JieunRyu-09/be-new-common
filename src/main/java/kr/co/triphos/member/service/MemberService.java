package kr.co.triphos.member.service;

import kr.co.triphos.member.Member;
import kr.co.triphos.member.MemberDTO;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import static java.lang.reflect.Modifier.*;

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
	public boolean createMember(Member member) throws Exception{
		String memberId = member.getMemberId();
		String memberPw = member.getMemberPw();
		Member existMember = memberRepository.findByMemberId(memberId).orElse(null);

		if (existMember != null) {throw new Exception("이미 등록된 사용자입니다.");}

		String encodedPassword = passwordEncoder.encode(member.getMemberPw());
		member.setMemberPw(encodedPassword);
		memberRepository.save(member);
		return true;
	}

	@Transactional
	public boolean updateMemberPw(MemberDTO memberDTO) throws Exception{
		String memberId 	= memberDTO.getMemberId();
		String memberPw 	= memberDTO.getMemberPw();
		String newMemberPw 	= memberDTO.getNewMemberPw();
		Member existMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));

		if (!passwordEncoder.matches(memberPw, existMember.getMemberPw())) {throw new Exception("잘못된 기존 비밀번호입니다.");}

		// 사용자가 입력한 정보만 업데이트
		for (Field field: MemberDTO.class.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				// static, final 필드는 제외
				if ((field.getModifiers() & (STATIC | FINAL)) != 0) continue;

				Object newValue = field.get(memberDTO);
				boolean fieldExist = fieldExists(existMember, field.getName());
				// 사용자가 값을 입력했고, memberEntity 에 해당 field 가 있으며, fieldName 이 memberPw가 아닌 경우
				if (newValue != null && fieldExist && !field.getName().equals("memberPw")) {
					Field targetField = existMember.getClass().getDeclaredField(field.getName());
					targetField.setAccessible(true);
					targetField.set(existMember, newValue);
				}
			} catch (IllegalAccessException e) {
				throw new Exception(e.toString());
			}
		}

		// 비밀번호 업데이트
		if (newMemberPw != null) {
			newMemberPw = passwordEncoder.encode(newMemberPw);
			existMember.setMemberPw(newMemberPw);;
		}
		memberRepository.save(existMember);
		return true;
	}

	// 엔티티에 해당 필드가 있는지 확인하는 메서드
	private boolean fieldExists(Object obj, String fieldName) {
		try {
			obj.getClass().getDeclaredField(fieldName);
			return true; // 필드가 존재하면 true 반환
		} catch (NoSuchFieldException e) {
			return false; // 필드가 없으면 false 반환
		}
	}

}
