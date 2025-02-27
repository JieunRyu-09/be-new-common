package kr.co.triphos.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByMemberId(String memberId);
	Member findByMemberIdAndMemberPw(String memberId, String memberPw);

}
