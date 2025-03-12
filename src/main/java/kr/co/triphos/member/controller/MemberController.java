package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.service.ResponseDTO;
import kr.co.triphos.member.dto.CustomUserDetailsDTO;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/update")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자정보 수정", description = "해당 api는 회원가입 및 로그인 등 구현 완료 후 삭제 예정")
	public ResponseEntity<?> updateMember(@Parameter(description = "사용자 Id")		@RequestParam String memberId,
										  @Parameter(description = "사용자 이름") 	@RequestParam String memberNm,
										  @Parameter(description = "사용자 Pw") 		@RequestParam String memberPw,
										  @Parameter(description = "사용자 신규Pw") 	@RequestParam String newMemberPw,
										  @Parameter(description = "사용자 이메일") 	@RequestParam String email,
										  @Parameter(description = "사용자 핸드폰") 	@RequestParam String phone,
										  @Parameter(description = "사용자 등급") 	@RequestParam String memberType,
										  @Parameter(description = "계정사용여부") 	@RequestParam String delYn,
										  @Parameter(description = "관리자여부")	 	@RequestParam String adminYn) {

		ResponseDTO responseDTO = new ResponseDTO();
		try {
			MemberDTO memberDTO = MemberDTO.updateMember()
					.memberId(memberId)
					.memberNm(memberNm)
					.memberPw(memberPw)
					.newMemberPw(newMemberPw)
					.email(email)
					.phone(phone)
					.memberType(memberType)
					.delYn(delYn)
					.adminYn(adminYn)
					.build();
			boolean res = memberService.updateMember(memberDTO);
			String msg = res ? "사용자 정보를 수정하였습니다." : "사용자 정보 수정에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/getMemberMenuList")
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자의 메뉴목록 조회", description = "메뉴목록만 조회")
	public ResponseEntity<?> getMemberMenuList(@Parameter(description = "사용자 Id")	@RequestParam String memberId) {

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			List<HashMap<String, Object>> menuList = memberService.getMemberMenuList(memberId);
			responseDTO.addData("menuList", menuList);
			responseDTO.setSuccess(true);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/getMenuMemberAuth")
	@Tag(name="사용자 권한")
	@Operation(summary = "메뉴의 사용자 권한 조회", description = "특정메뉴의 CRUD 권한 조회")
	public ResponseEntity<?> getMenuMemberAuth(@Parameter(description = "사용자 Id")	@RequestParam String memberId,
										  	   @Parameter(description = "메뉴 Id")	@RequestParam String menuId) {

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			HashMap<String, Object> resultItem = memberService.getMenuMemberAuth(memberId, menuId);
			responseDTO.addData("menuMemberAuth", resultItem);
			responseDTO.setSuccess(true);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/getMenuMemberAuthList")
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자의 메뉴권한 전체목록 조회", description = "전체 메뉴의 CRUD 권한 조회")
	public ResponseEntity<?> getMenuMemberAuthList(@Parameter(description = "사용자 Id")	@RequestParam String memberId) {

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			List<HashMap<String, Object>> menuList = memberService.getMenuMemberAuthList(memberId);
			responseDTO.addData("menuList", menuList);
			responseDTO.setSuccess(true);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/updateMenuMemberAuth")
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자의 메뉴권한 수정", description = "전체 메뉴에 대한 권한 수정")
	public ResponseEntity<?> updateMenuMemberAuth(@RequestBody List<MenuMemberAuthDTO> authList, @AuthenticationPrincipal UserDetails userDetails) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			boolean success = memberService.updateMenuMemberAuth(authList, userDetails.getUsername());
			String msg = success ? "사용자 권한을 수정하였습니다." : "사용자 권한수정에 실패하였습니다.";
			responseDTO.setSuccess(success);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

}
