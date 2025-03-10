package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.service.ResponseDTO;
import kr.co.triphos.member.dto.memberDTO.MemberDTO;
import kr.co.triphos.member.dto.memberDTO.MemberUpdateDTO;
import kr.co.triphos.member.service.AuthService;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static kr.co.triphos.common.service.CommonFunc.isBlank;


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
	public ResponseEntity<?> login(@Parameter(description = "사용자Id") @RequestParam String id,  @Parameter(description = "사용자Pw") @RequestParam String password) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String accessToken = authService.login(id, password);

			responseDTO.setSuccess(true);
			responseDTO.addData("accessToken", accessToken);
			responseDTO.addData("refreshToken", authService.getRefreshToken(accessToken));
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
		Map<String, Object> response = new HashMap<>();
		response.put("res", false);

		try {
			String newAccessToken = authService.refreshAccessToken(refreshToken);
			String newRefreshToken = authService.getRefreshToken(newAccessToken);

			response.put("res", true);
			response.put("accessToken", newAccessToken);
			response.put("refreshToken", newRefreshToken);

			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
					.body(response);
		}
		catch (Exception ex) {
			return ResponseEntity.status(401).body(ex.getMessage());
		}
	}

	@GetMapping("/loginPage")
	@Hidden
	public ModelAndView loginPage(Model model) {
		return new ModelAndView("member/login");
	}

	@GetMapping("/checkExistMemberId")
	@Tag(name="사용자 관리")
	@Operation(summary = "존재하는 ID 조회", description = "사용자 회원가입 시 중복되는 ID 확인")
	public boolean checkExistMemberId(@RequestParam String memberId) {
		return memberService.getMemberInfoById(memberId) != null;
	}

	@PostMapping("/create")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자 회원가입", description = "")
	public ResponseEntity<?> createMember(@RequestBody @Valid MemberDTO memberDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("res", false);

		try {
			if (isBlank(memberDTO.getMemberPw())) response.put("msg", "비밀번호가 없습니다.");
			if (isBlank(memberDTO.getMemberNm())) response.put("msg", "이름이 없습니다.");

			boolean result = memberService.createMember(memberDTO);
			String msg = result ? "사용자를 생성하였습니다." : "사용자 생성에 실패하였습니다.";
			response.put("res", result);
			response.put("msg", msg);
		}
		catch (Exception ex) {
			log.error(ex);
			response.put("msg", ex.getMessage());
		}
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/update")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자정보 수정", description = "해당 api는 회원가입 및 로그인 등 구현 완료 후 삭제 예정")
	public boolean updateMember(@RequestBody MemberUpdateDTO memberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			responseDTO.setSuccess(true);
			responseDTO.setMsg("로그인에 성공하였습니다");
			return memberService.updateMemberPw(memberDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}
}
