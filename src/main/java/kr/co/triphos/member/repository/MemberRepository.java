package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByMemberId(String memberId);

	Member findByMemberIdAndMemberPw(String memberId, String memberPw);

	List<Member> findByMemberIdLikeAndMemberNmLike(String memberId, String memberNm);

	List<Member> findByMemberNmLike(String memberNm);

	List<Member> findByMemberIdLike(String memberId);
}
