package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.service.AuthService;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
//@EnableWebMvc
public class AuthController {
	private final AuthService authService;
	private final MemberService memberService;

	@PostMapping("/login")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자 로그인", description = "")
	@ApiResponse(content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponseDTO.class)
	))
	public ResponseEntity<?> login(@Parameter(description = "사용자Id") @RequestParam String id,
								   @Parameter(description = "사용자Pw") @RequestParam String password) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String accessToken = authService.login(id, password);
			List<HashMap<String, Object>> menuList = authService.getMemberMenuList(id);

			responseDTO.setSuccess(true);
			responseDTO.addData("accessToken", accessToken);
			responseDTO.addData("refreshToken", authService.getRefreshToken(accessToken));
			responseDTO.addData("menuList", menuList);
			responseDTO.setMsg("로그인에 성공하였습니다");

			return ResponseEntity.ok()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.body(responseDTO);
		}
		catch (RuntimeException re) {
			responseDTO.setMsg(re.getMessage());
			return ResponseEntity.status(401).body(responseDTO);
		}
		catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.status(401).body("Invalid credentials");
		}
	}

	@PostMapping("/refresh")
	@Hidden
	public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String newAccessToken = authService.refreshAccessToken(refreshToken);
			String newRefreshToken = authService.getRefreshToken(newAccessToken);

			responseDTO.setSuccess(true);
			responseDTO.addData("accessToken", newAccessToken);
			responseDTO.addData("refreshToken", newRefreshToken);

			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
					.body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.status(401).body(ex.getMessage());
		}
	}

	@GetMapping("/checkExistMemberId")
	@Tag(name="사용자 관리")
	@Operation(summary = "존재하는 ID 조회", description = "사용자 회원가입 시 중복되는 ID 확인")
	public ResponseEntity<?> checkExistMemberId(@RequestParam String memberId) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			responseDTO.setSuccess(true);
			responseDTO.addData("result", memberService.getMemberInfoById(memberId) != null);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/create")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자 회원가입", description = "")
	public ResponseEntity<?> createMember(@Parameter(description = "사용자 Id")		@RequestParam String memberId,
										  @Parameter(description = "사용자 Pw") 		@RequestParam String memberPw,
										  @Parameter(description = "사용자 이름") 	@RequestParam String memberNm,
										  @Parameter(description = "사용자 이메일") 	@RequestParam String email,
										  @Parameter(description = "사용자 핸드폰") 	@RequestParam String phone,
										  @Parameter(description = "사용자 등급") 	@RequestParam String memberType) {

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			MemberDTO memberDTO = MemberDTO.createMember()
					.memberId(memberId)
					.memberNm(memberNm)
					.memberPw(memberPw)
					.email(email)
					.phone(phone)
					.memberType(memberType)
					.build();
			boolean result = memberService.createMember(memberDTO);
			String msg = result ? "사용자를 생성하였습니다." : "사용자 생성에 실패하였습니다.";
			responseDTO.setSuccess(result);
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
