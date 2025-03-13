package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByMemberId(String memberId);

	MemberEntity findByMemberIdAndMemberPw(String memberId, String memberPw);

	List<MemberEntity> findByMemberIdLikeAndMemberNmLike(String memberId, String memberNm);

	List<MemberEntity> findByMemberNmLike(String memberNm);

	List<MemberEntity> findByMemberIdLike(String memberId);
}
