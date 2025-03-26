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
import kr.co.triphos.member.service.NewMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/members")
@RequiredArgsConstructor
@Log4j2
public class NewMemberController {
	private final NewMemberService memberService;
	private final AuthenticationFacadeService authFacadeService;

	@GetMapping("/token")
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

	@GetMapping(value={"/{memberId}", ""})
	@Tag(name="사용자 관리", description = "회원 정보 관련 API")
	@Operation(summary = "사용자정보 조회", description = "")
	public ResponseEntity<?> updateMember(@Parameter(description = "사용자 ID") @PathVariable(required = false) String memberId) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			memberId = memberId == null ? authFacadeService.getMemberId() : memberId;
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

	@PutMapping("")
	@Tag(name = "사용자 관리")
	@Operation(
			summary = "사용자 정보 수정",
			description = "사용자의 정보를 수정합니다.(비밀번호 검증x)",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "사용자정보 수정 예시", ref = "#/components/examples/member.put.info")
					)
			)
	)
	public ResponseEntity<?> updateInfo(@RequestBody MemberDTO memberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			boolean res = memberService.updateInfo(memberDTO);
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

	@PutMapping("/my-info")
	@Tag(name = "사용자 관리")
	@Operation(
			summary = "본인 정보 수정",
			description = "본인의 정보를 수정합니다.(비밀번호 검증O)",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "본인 수정 예시", ref = "#/components/examples/member.put.myInfo")
					)
			)
	)
	public ResponseEntity<?> updateMyInfo(@RequestBody MemberDTO memberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			boolean res = memberService.updateMyInfo(memberDTO);
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

	@GetMapping("/menu-list")
	@Tag(name="사용자 권한", description = "사용자 권한 관련 API")
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

	@GetMapping("/list")
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자 목록 조회", description = "회원 권한 관련 API")
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

	@GetMapping(value={"/{memberId}/menu-auth", "/menu-auth"})
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자의 메뉴권한 조회", description = "회원 권한 관련 API")
	public ResponseEntity<?> getMemberMenuAuth(@Parameter(description = "사용자 Id")	@PathVariable(required = false) String memberId) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			memberId = memberId == null ? authFacadeService.getMemberId() : memberId;
			List<HashMap<String, Object>> menuList = memberService.getMenuMemberAuth(memberId, null);
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

	@GetMapping("/menu-auth/{menuId}")
	@Tag(name="사용자 권한")
	@Operation(summary = "사용자의 메뉴권한 목록조회", description = "회원 권한 관련 API")
	public ResponseEntity<?> getMemberMenuAuthList(@Parameter(description = "사용자 Id")	@PathVariable(required = false) String memberId,
												   @Parameter(description = "메뉴 Id")	@PathVariable(required = false) String menuId) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			memberId = memberId == null ? authFacadeService.getMemberId() : memberId;
			HashMap<String, Object> menuMemberAuth = memberService.getMenuMemberAuth(memberId, menuId).get(0);
			responseDTO.addData("menuMemberAuth", menuMemberAuth);
			responseDTO.setSuccess(true);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PutMapping("/menu-auth")
	@Tag(name="사용자 권한", description = "회원 권한 관련 API")
	@Operation(
			summary = "사용자의 메뉴권한 수정",
			description = "전체 메뉴에 대한 권한 수정",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "사용자정보 수정 예시", ref = "#/components/examples/member.put.menuAuth")
					)
			))
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

}
