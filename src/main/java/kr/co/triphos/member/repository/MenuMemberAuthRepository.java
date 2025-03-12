package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.MenuMemberAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuMemberAuthRepository extends JpaRepository<MenuMemberAuthEntity, Long> {
	List<MenuMemberAuthEntity> findByPkMemberIdAndUseYn(String memberId, String useYn);

	List<MenuMemberAuthEntity> findByPkMemberId(String memberId);

	MenuMemberAuthEntity findByPkMenuIdAndPkMemberId(String menuId, String memberId);
}
