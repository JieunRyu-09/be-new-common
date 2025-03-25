package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.MenuMemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MenuMemberAuthRepository extends JpaRepository<MenuMemberAuth, Long> {
	List<MenuMemberAuth> findByPkMemberIdAndUseYn(String memberId, String useYn);

	List<MenuMemberAuth> findByPkMemberId(String memberId);

	Optional<MenuMemberAuth> findByPkMenuIdAndPkMemberId(String menuId, String memberId);
}
