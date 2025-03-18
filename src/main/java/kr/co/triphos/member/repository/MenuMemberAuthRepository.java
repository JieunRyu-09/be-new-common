package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.MenuMemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuMemberAuthRepository extends JpaRepository<MenuMemberAuth, Long> {
	List<MenuMemberAuth> findByPkMemberIdAndUseYn(String memberId, String useYn);

	List<MenuMemberAuth> findByPkMemberId(String memberId);

	MenuMemberAuth findByPkMenuIdAndPkMemberId(String menuId, String memberId);
}
