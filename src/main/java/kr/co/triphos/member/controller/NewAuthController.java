package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.dto.TokenDTO;
import kr.co.triphos.member.service.AuthService;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Log4j2
//@EnableWebMvc
public class NewAuthController {
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
			HashMap<String, String> accessTokenMap = authService.login(id, password);
			List<HashMap<String, Object>> menuList = authService.getMemberMenuList(id);
			String accessToken 	= accessTokenMap.get("accessToken");
			String expiresIn 	= accessTokenMap.get("expirationDate");

			responseDTO.addData("accessToken", accessToken);
			responseDTO.addData("refreshToken", authService.getRefreshToken(accessToken));
			responseDTO.addData("expiresIn", expiresIn);
			responseDTO.addData("menuList", menuList);
			responseDTO.setMsg("로그인에 성공하였습니다");
			responseDTO.setSuccess(true);
			return ResponseEntity.ok()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.body(responseDTO);
		}
		catch (DataAccessException de) {
			log.error(de.getMessage());
			responseDTO.setMsg("서버현황이 불안정합니다. 잠시 후 다시 시도하여주십시오.");
			return ResponseEntity.status(408).body(responseDTO);
		}
		catch (RuntimeException re) {
			log.error(re.getMessage());
			responseDTO.setMsg(re.getMessage());
			return ResponseEntity.status(401).body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.status(401).body("Invalid credentials");
		}
	}

	@GetMapping("/check-id")
	@Tag(name="사용자 관리")
	@Operation(summary = "존재하는 ID 조회", description = "사용자 회원가입 시 중복되는 ID 확인")
	public ResponseEntity<?> checkExistMemberId(@RequestParam String memberId) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			responseDTO.setSuccess(true);
			responseDTO.addData("result", memberService.getMemberInfoById(memberId) != null);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (DataAccessException de) {
			log.error(de.getMessage());
			responseDTO.setMsg("서버현황이 불안정합니다. 잠시 후 다시 시도하여주십시오.");
			return ResponseEntity.status(408).body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/members")
	@Tag(name="사용자 관리")
	@Operation(
			summary = "사용자 회원가입",
			description = "",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "사용자 회원가입 예시", ref = "#/components/examples/auth.post.member.info")
					)
			))
	public ResponseEntity<?> createMember(@RequestBody MemberDTO memberDTO) {

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			boolean result = memberService.createMember(memberDTO);
			String msg = result ? "사용자를 생성하였습니다." : "사용자 생성에 실패하였습니다.";
			responseDTO.setSuccess(result);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (DataAccessException de) {
			log.error(de.getMessage());
			responseDTO.setMsg("서버현황이 불안정합니다. 잠시 후 다시 시도하여주십시오.");
			return ResponseEntity.status(408).body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/refresh")
	@Tag(name="JWT 토큰")
	@Operation(summary = "토큰 재발행", description = "accessToken, refreshToken 재발행")
	public ResponseEntity<?> refreshToken(@Parameter(description = "refreshToken") @RequestBody TokenDTO dto) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			HashMap<String, String> newAccessTokenMap = authService.refreshAccessToken(dto.getRefreshToken());
			String newAccessToken 	= newAccessTokenMap.get("accessToken");
			String newRefreshToken 	= authService.getRefreshToken(newAccessToken);
			String expiresIn 		= newAccessTokenMap.get("expirationDate");

			responseDTO.setSuccess(true);
			responseDTO.addData("accessToken", newAccessToken);
			responseDTO.addData("refreshToken", newRefreshToken);
			responseDTO.addData("expiresIn", expiresIn);
			responseDTO.setMsg("토큰을 재발행하였습니다.");

			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
					.body(responseDTO);
		}
		catch (DataAccessException de) {
			log.error(de.getMessage());
			responseDTO.setMsg("서버현황이 불안정합니다. 잠시 후 다시 시도하여주십시오.");
			return ResponseEntity.status(408).body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.status(401).body(ex.getMessage());
		}
	}
}
