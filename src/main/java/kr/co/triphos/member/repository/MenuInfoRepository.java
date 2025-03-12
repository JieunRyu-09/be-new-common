package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.MemberEntity;
import kr.co.triphos.member.entity.MenuInfoEntity;
import kr.co.triphos.member.entity.MenuMemberAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuInfoRepository extends JpaRepository<MenuInfoEntity, Long> {
	List<MenuInfoEntity> findByDisplayYn(String displayYn);
}
