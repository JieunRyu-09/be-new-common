package kr.co.triphos.member.service;

import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.entity.MemberEntity;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.entity.MenuInfoEntity;
import kr.co.triphos.member.entity.MenuMemberAuthEntity;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.member.repository.MenuInfoRepository;
import kr.co.triphos.member.repository.MenuMemberAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final MenuMemberAuthRepository menuMemberAuthRepository;
	private final MenuInfoRepository menuInfoRepository;

	public MemberEntity getMemberInfoById(String memberId) {
		return memberRepository.findByMemberId(memberId).orElse(null);
	}

	public MemberEntity getMemberInfoByIdAndPw(String memberId, String memberPw) {
		return memberRepository.findByMemberIdAndMemberPw(memberId, memberPw);
	}

	@Transactional
	public boolean createMember(MemberDTO memberDTO) throws Exception{
		String memberId = memberDTO.getMemberId();
		String memberPw = memberDTO.getMemberPw();
		MemberEntity existMemberEntity = memberRepository.findByMemberId(memberId).orElse(new MemberEntity());

		if (existMemberEntity.getMemberId() != null) {throw new Exception("이미 등록된 사용자입니다.");}

		MemberEntity newMemberEntity = new MemberEntity(memberDTO);
		newMemberEntity.setMemberPw(passwordEncoder.encode(memberDTO.getMemberPw()));
		newMemberEntity.setMemberStatus("MMS001");
		newMemberEntity.setAdminYn("N");
		newMemberEntity.setDelYn("N");
		newMemberEntity.setInsMember(memberDTO.getMemberId());
		memberRepository.save(newMemberEntity);
		return true;
	}

	@Transactional
	public boolean updateMember(MemberDTO memberDTO) throws Exception{
		String memberId 	= memberDTO.getMemberId();
		String memberPw 	= memberDTO.getMemberPw();
		String newMemberPw 	= memberDTO.getNewMemberPw();
		MemberEntity existMemberEntity = memberRepository.findByMemberId(memberId).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));
		if (!passwordEncoder.matches(memberPw, existMemberEntity.getMemberPw())) {throw new Exception("잘못된 기존 비밀번호입니다.");}

		try {
			// 사용자가 입력한 정보만 업데이트
			existMemberEntity.updateMember(memberDTO);
			// 비밀번호 업데이트
			if (newMemberPw != null) {
				newMemberPw = passwordEncoder.encode(newMemberPw);
				existMemberEntity.setMemberPw(newMemberPw);;
			}
			memberRepository.save(existMemberEntity);
			return true;
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	public HashMap<String, Object> getMenuMemberAuth (String memberId, String menuId) throws Exception {
		HashMap<String, Object> resultItem = new HashMap<>();

		MenuMemberAuthEntity authItem = menuMemberAuthRepository.findByPkMenuIdAndPkMemberId(menuId, memberId);
		resultItem.put("menuId", 		authItem.getPk().getMenuId());
		resultItem.put("memberId", 		authItem.getPk().getMemberId());
		resultItem.put("useYn", 		authItem.getUseYn());
		resultItem.put("authSearch", 	authItem.getAuthSearch());
		resultItem.put("authIns", 		authItem.getAuthIns());
		resultItem.put("authDel", 		authItem.getAuthDel());
		resultItem.put("authMod", 		authItem.getAuthMod());
		resultItem.put("excelExport", 	authItem.getExcelExport());
		return resultItem;
	}

	public List<HashMap<String, Object>> getMemberMenuList(String id) {
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		List<MenuInfoEntity> menuList = menuInfoRepository.findAll();
		List<MenuMemberAuthEntity> authList = menuMemberAuthRepository.findByPkMemberIdAndUseYn(id, "Y");

		menuList.forEach(menuItem -> {
			String useYn = authList.stream().filter(authItem ->
							menuItem.getMenuId().equals(authItem.getPk().getMenuId()))
					.findFirst().map(MenuMemberAuthEntity::getUseYn)
					.orElse("N");

			if (useYn.equals("Y")) {
				HashMap<String, Object> resultItem = new HashMap<>();
				resultItem.put("mainCd",    menuItem.getPk().getMainCd());
				resultItem.put("sub1Cd",    menuItem.getPk().getSub1Cd());
				resultItem.put("sub2Cd",    menuItem.getPk().getSub2Cd());
				resultItem.put("orderBy",   menuItem.getOrderBy());
				resultItem.put("menuId",    menuItem.getMenuId());
				resultItem.put("menuTitle", menuItem.getMenuTitle());
				resultItem.put("menuLink",  menuItem.getMenuLink());
				resultItem.put("displayYn", menuItem.getDisplayYn());
				resultList.add(resultItem);
			}
		});
		return resultList;
	}

	public List<HashMap<String, Object>> getMenuMemberAuthList(String id) {
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		List<MenuInfoEntity> menuList = menuInfoRepository.findByDisplayYn("Y");
		List<MenuMemberAuthEntity> authList = menuMemberAuthRepository.findByPkMemberIdAndUseYn(id, "Y");

		menuList.forEach(menuItem -> {
			MenuMemberAuthEntity authItem = authList.stream().filter(authListItem ->
							menuItem.getMenuId().equals(authListItem.getPk().getMenuId()))
							.findFirst().orElse(new MenuMemberAuthEntity());
			String useYn = authItem.getUseYn();

			HashMap<String, Object> resultItem = new HashMap<>();
			if (useYn != null && useYn.equals("Y")) {
				// menuInfo
				resultItem.put("mainCd",    menuItem.getPk().getMainCd());
				resultItem.put("sub1Cd",    menuItem.getPk().getMainCd());
				resultItem.put("sub2Cd",    menuItem.getPk().getMainCd());
				resultItem.put("orderBy",   menuItem.getOrderBy());
				resultItem.put("menuId",    menuItem.getMenuId());
				// authInfo
				resultItem.put("memberId",  	authItem.getPk().getMemberId());
				resultItem.put("useYn",    		authItem.getUseYn());
				resultItem.put("authSearch",    authItem.getAuthSearch());
				resultItem.put("authIns",    	authItem.getAuthIns());
				resultItem.put("authDel",    	authItem.getAuthDel());
				resultItem.put("authMod",    	authItem.getAuthMod());
				resultItem.put("excelExport",	authItem.getExcelExport());
				resultList.add(resultItem);
			}
		});
		return resultList;
	}

	@Transactional
	public boolean updateMenuMemberAuth(List<MenuMemberAuthDTO> dtoAuthList, String modId) {
		List<MenuMemberAuthEntity> authList = new ArrayList<>();

		try {
			dtoAuthList.forEach(dtoAuthItem -> {
				String memberId = dtoAuthItem.getMemberId();
				String menuId 	= dtoAuthItem.getMenuId();

				MenuMemberAuthEntity entityAuthItem = menuMemberAuthRepository.findByPkMenuIdAndPkMemberId(menuId, memberId);
				entityAuthItem.updateMenuMemberAuth(dtoAuthItem);
				authList.add(entityAuthItem);
			});
			menuMemberAuthRepository.saveAll(authList);https://checkout3.officekeeper.co.kr/alert?Culture=1042&AlertSiteInfo=3b5c8684ca76879a3d15c178934f37d6%7Cgoogle.com%7C%2Fsearch%3Fq%3Dsecurity%2Bbearer%26oq%3Dsecurity%2Bbearer%26gs_lcrp%3DEgZjaHJvbWUyCwgAEEUYExg5GIAEMggIARAAGBMYHjIICAIQABgTGB4yCAgDEAAYExgeMgoIBBAAGAUYExgeMgoIBRAAGAUYExgeMgoIBhAAGAUYExgeMgoIBxAAGAUYExgeMgoICBAAGAUYExgeMgoICRAAGAUYExge0gEINjQzOWowajeoAgCwAgA%26sourceid%3Dchrome%26ie%3DUTF-8%7Cgoogle.com%7CURLHost%7CWarning%7C0%7C7496%7C16030200%7C58%7C0e81b7236bc8cc68%7C273980%7Cheper%7C230&AlertSiteInfoDigest=afbd1aa52ec89a0bfc3448224f920544f47e5f08a0227b629641847037812b5d
			return true;
		}
		catch (RuntimeException ex) {
			log.error(ex);
			throw new RuntimeException("사용자 권한수정에 실패하였습니다.");
		}
	}


}
