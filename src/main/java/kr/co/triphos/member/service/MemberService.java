package kr.co.triphos.member.service;

import kr.co.triphos.common.entity.MenuInfo;
import kr.co.triphos.common.repository.MenuInfoRepository;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.entity.MenuMemberAuth;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.member.repository.MenuMemberAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final MenuMemberAuthRepository menuMemberAuthRepository;
	private final MenuInfoRepository menuInfoRepository;

	public Member getMemberInfoById(String memberId) {
		return memberRepository.findByMemberId(memberId).orElse(null);
	}

	public Member getMemberInfoByIdAndPw(String memberId, String memberPw) {
		return memberRepository.findByMemberIdAndMemberPw(memberId, memberPw);
	}

	public MemberDTO getMemberInfo(String memberId) {
		Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new RuntimeException("사용자 정보를 찾지 못하였습니다."));
		member.setMemberPw(null);
		return new MemberDTO(member);
	}

	@Transactional
	public boolean createMember(MemberDTO memberDTO) throws Exception {
		String memberId = memberDTO.getMemberId();
		String memberPw = memberDTO.getMemberPw();
		Member existMemberEntity = memberRepository.findByMemberId(memberId).orElse(new Member());

		if (existMemberEntity.getMemberId() != null) {throw new Exception("이미 등록된 사용자입니다.");}

		Member newMemberEntity = new Member(memberDTO);
		newMemberEntity.setMemberPw(passwordEncoder.encode(memberDTO.getMemberPw()));
		newMemberEntity.setMemberStatus("MMS001");
		newMemberEntity.setAdminYn("N");
		newMemberEntity.setDelYn("N");
		newMemberEntity.setInsMember(memberDTO.getMemberId());
		memberRepository.save(newMemberEntity);
		return true;
	}

	@Transactional
	public boolean updateMyInfo(MemberDTO memberDTO) throws Exception{
		String memberId 	= memberDTO.getMemberId();
		String memberPw 	= memberDTO.getMemberPw();
		String newMemberPw 	= memberDTO.getNewMemberPw();
		Member existMemberEntity = memberRepository.findByMemberId(memberId).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));
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

	@Transactional
	/**
	 * 타인의 정보를 수정.
	 * 기존 비밀번호 확인 X
	 */
	public boolean updateInfo(MemberDTO memberDTO) throws Exception{
		String memberId 	= memberDTO.getMemberId();
		String newMemberPw 	= memberDTO.getNewMemberPw();
		Member existMemberEntity = memberRepository.findByMemberId(memberId).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));

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


	public List<HashMap<String, Object>> getMenuMemberAuth(String memberId, String menuId) throws Exception {
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		List<MenuInfo> menuList;
		menuList = menuInfoRepository.findByDisplayYn("Y");

		List<MenuMemberAuth> authList = menuMemberAuthRepository.findByPkMemberId(memberId);

		menuList.forEach(menuItem -> {
			// menuId가 특정되어있는 경우 비교실행
			if (menuId != null && !menuId.isEmpty()) {
				if (!menuItem.getMenuId().equals(menuId)) return;
			}
			MenuMemberAuth authItem = authList.stream().filter(authListItem ->
							menuItem.getMenuId().equals(authListItem.getPk().getMenuId()))
					.findFirst().orElse(new MenuMemberAuth());
			MenuInfo subMenuItem = menuList.stream().filter(subMenu ->
					subMenu.getPk().getMainCd() == menuItem.getPk().getMainCd() &&
							subMenu.getPk().getSub1Cd() == menuItem.getPk().getSub1Cd() &&
							subMenu.getPk().getSub2Cd() == 0
			).findFirst().orElse(null);
			assert subMenuItem != null;

			HashMap<String, Object> resultItem = new LinkedHashMap<>();
			String useYn		= authItem.getUseYn() 		!= null ? authItem.getUseYn() 		: "N";
			String authSearch	= authItem.getAuthSearch() 	!= null ? authItem.getAuthSearch() 	: "N";
			String authIns		= authItem.getAuthIns() 	!= null ? authItem.getAuthIns() 	: "N";
			String authDel		= authItem.getAuthDel() 	!= null ? authItem.getAuthDel() 	: "N";
			String authMod		= authItem.getAuthMod() 	!= null ? authItem.getAuthMod() 	: "N";
			String excelExport	= authItem.getExcelExport() != null ? authItem.getExcelExport() : "N";
			String mainTitle	= subMenuItem.getMenuTitle();
			String subTitle		= menuItem.getPk().getSub2Cd() == 0 ? null : menuItem.getMenuTitle();
			// menuInfo
			resultItem.put("mainCd",    menuItem.getPk().getMainCd());
			resultItem.put("sub1Cd",    menuItem.getPk().getSub1Cd());
			resultItem.put("sub2Cd",    menuItem.getPk().getSub2Cd());
			resultItem.put("orderBy",   menuItem.getOrderBy());
			resultItem.put("menuId",    menuItem.getMenuId());
			resultItem.put("mainTitle", mainTitle);
			resultItem.put("menuTitle", menuItem.getMenuTitle());
			resultItem.put("subTitle",  subTitle);
			// authInfo
			resultItem.put("memberId",  	memberId);
			resultItem.put("useYn",    		useYn);
			resultItem.put("authSearch",    authSearch);
			resultItem.put("authIns",    	authIns);
			resultItem.put("authDel",    	authDel);
			resultItem.put("authMod",    	authMod);
			resultItem.put("excelExport",	excelExport);
			resultList.add(resultItem);
		});
		return resultList;
	}

	public List<HashMap<String, String>> getMemberList(String memberId, String memberNm) throws Exception {
		List<HashMap<String, String>> resultList = new ArrayList<>();
		try {
			List<Member> entityList = null;
			if (memberId != null && memberNm != null) {
				entityList = memberRepository.findByMemberIdLikeAndMemberNmLike(memberId, memberNm);
			}
			else if (memberId != null) {
				entityList = memberRepository.findByMemberIdLike("%" + memberId + "%");
			}
			else if (memberNm != null) {
				entityList = memberRepository.findByMemberNmLike("%" + memberNm + "%");
			}
			else {
				entityList = memberRepository.findAll();
			}
			entityList.forEach(entityItem -> {
				HashMap<String, String> resultItem = new HashMap<>();
				resultItem.put("memberId", entityItem.getMemberId());
				resultItem.put("memberNm", entityItem.getMemberNm());
				resultList.add(resultItem);
			});
			return resultList;
		}
		catch (RuntimeException ex) {
			log.error(ex);
			throw new RuntimeException("사용자 목록조회에 실패하였습니다.");
		}
	}

	@Transactional
	public boolean updateMenuMemberAuth(List<MenuMemberAuthDTO> dtoAuthList, String modId) throws Exception {
		List<MenuMemberAuth> authList = new ArrayList<>();

		dtoAuthList.forEach(dtoAuthItem -> {
			String memberId = dtoAuthItem.getMemberId();
			String menuId 	= dtoAuthItem.getMenuId();

			MenuMemberAuth entityAuthItem = menuMemberAuthRepository.findByPkMenuIdAndPkMemberId(menuId, memberId)
					.orElseThrow(() -> new RuntimeException("사용자/권한 정보를 찾지 못하였습니다."));
			entityAuthItem.updateMenuMemberAuth(dtoAuthItem);
			authList.add(entityAuthItem);
		});
		menuMemberAuthRepository.saveAll(authList);
		return true;
	}

	public List<HashMap<String, Object>> getMemberMenuList(String id) throws Exception {
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		List<MenuInfo> menuList = menuInfoRepository.findAll();
		List<MenuMemberAuth> authList = menuMemberAuthRepository.findByPkMemberIdAndUseYn(id, "Y");

		menuList.forEach(menuItem -> {
			String useYn = authList.stream().filter(authItem ->
							menuItem.getMenuId().equals(authItem.getPk().getMenuId()))
					.findFirst().map(MenuMemberAuth::getUseYn)
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
}
