package kr.co.triphos.member.repository;

import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.entity.MenuInfo;
import kr.co.triphos.member.entity.MenuMemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuInfoRepository extends JpaRepository<MenuInfo, Long> {
	List<MenuInfo> findByDisplayYn(String displayYn);
}
