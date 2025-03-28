package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {
	private final MemberService memberService;
	private final AuthenticationFacadeService authFacadeService;

	@GetMapping("/tokenCheck")
	@Tag(name = "JWT 토큰")
	@Operation(summary = "토큰 유효기간 확인", description = "")
	public ResponseEntity<?> checkTokenValid () {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authFacadeService.getMemberId();

			responseDTO.setSuccess(true);
			responseDTO.setMsg("토큰유효");
			responseDTO.addData("id", memberId);
			responseDTO.addData("ServerTime", LocalDateTime.now());
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/getMemberInfo")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자정보 조회", description = "")
	public ResponseEntity<?> updateMember(@Parameter(description = "사용자 ID") @RequestParam(required = false) String memberId) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (memberId == null) memberId = authFacadeService.getMemberId();
			MemberDTO memberDTO = memberService.getMemberInfo(memberId);
			responseDTO.addData("memberInfo", memberDTO);
			responseDTO.setSuccess(true);
			responseDTO.setMsg("사용자정보 조회 성공");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/updateMember")
	@Tag(name = "사용자 관리", description = "회원 정보 관련 API")
	@Operation(
		summary = "사용자 정보 수정",
		description = "사용자의 정보를 수정합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "수정할 사용자 정보",
			content = @Content(
				schema = @Schema(hidden = true),
				examples = @ExampleObject(name = "사용자정보 수정 예시", ref = "#/components/examples/member.put.info")
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "수정 성공",
				content = @Content(schema = @Schema(implementation = ResponseDTO.class))
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 요청",
				content = @Content(schema = @Schema(implementation = ResponseDTO.class))
			)
		}
	)
	public ResponseEntity<?> updateMember(@RequestBody MemberDTO memberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
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
	public ResponseEntity<?> getMemberMenuList() {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String memberId = authFacadeService.getMemberId();
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
	public ResponseEntity<?> getMenuMemberAuth(@Parameter(description = "메뉴 Id")	@RequestParam String menuId) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String memberId = authFacadeService.getMemberId();
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
	public ResponseEntity<?> updateMenuMemberAuth(@RequestBody List<MenuMemberAuthDTO> authList) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String memberId = authFacadeService.getMemberId();
			boolean success = memberService.updateMenuMemberAuth(authList, memberId);
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

	@GetMapping("/getMemberList")
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자 목록 조회", description = "")
	public ResponseEntity<?> getMemberList(@Parameter(description = "사용자 ID") @RequestParam(required = false) String memberId,
												   @Parameter(description = "사용자 이름") @RequestParam(required = false) String memberNm) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			List<HashMap<String, String>> memberList = memberService.getMemberList(memberId, memberNm);
			responseDTO.addData("memberList", memberList);
			responseDTO.setSuccess(true);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}
}
