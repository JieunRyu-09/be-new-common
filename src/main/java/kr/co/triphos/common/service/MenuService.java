package kr.co.triphos.common.service;


import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.MenuInfoDTO;
import kr.co.triphos.common.entity.ExcelData;
import kr.co.triphos.common.entity.ExcelInfo;
import kr.co.triphos.common.entity.MenuInfo;
import kr.co.triphos.common.repository.MenuInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Log4j2
public class MenuService {
	private final MenuInfoRepository menuInfoRepository;

	@Transactional
	public boolean updateMenu(List<MenuInfoDTO> menuInfoDTOList, String memberId) throws Exception {
		List<MenuInfo> updateMenuInfoEntityList = new ArrayList<>();
		List<MenuInfo> menuInfoEntityList = menuInfoRepository.findAll();
		LocalDateTime nowDate = LocalDateTime.now();

		menuInfoDTOList.forEach(menuInfoDTOItem -> {
			menuInfoEntityList.stream().filter(menuInfoEntityItem ->
							Objects.equals(menuInfoEntityItem.getPk().getMainCd(), menuInfoDTOItem.getMainCd()) &&
							Objects.equals(menuInfoEntityItem.getPk().getSub1Cd(), menuInfoDTOItem.getSub1Cd()) &&
							Objects.equals(menuInfoEntityItem.getPk().getSub2Cd(), menuInfoDTOItem.getSub2Cd())
					).findFirst().ifPresent(menuInfoEntityItem -> {
				menuInfoEntityItem.setOrderBy(menuInfoDTOItem.getOrderBy());
				menuInfoEntityItem.setMenuTitle(menuInfoDTOItem.getMenuTitle());
				menuInfoEntityItem.setUpdDt(nowDate);
				menuInfoEntityItem.setUpdMember(memberId);
				menuInfoEntityItem.setDisplayYn(menuInfoDTOItem.getDisplayYn());
				updateMenuInfoEntityList.add(menuInfoEntityItem);
			});
		});
		menuInfoRepository.saveAll(updateMenuInfoEntityList);
		return true;
	}
}
