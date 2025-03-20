package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.MenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuInfoRepository extends JpaRepository<MenuInfo, Long> {
	List<MenuInfo> findByDisplayYn(String displayYn);

	MenuInfo findByMenuId(String menuId);
}
