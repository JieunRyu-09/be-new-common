package kr.co.triphos.member.repository;

import kr.co.triphos.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByMemberId(String memberId);

	Member findByMemberIdAndMemberPw(String memberId, String memberPw);
}
