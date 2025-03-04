package kr.co.triphos.member.controller;

import kr.co.triphos.member.MemberDTO;
import kr.co.triphos.member.service.AuthService;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static kr.co.triphos.common.service.CommonFunc.isBlank;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
//@EnableWebMvc
public class AuthController {
	private final AuthService authService;
	private final MemberService memberService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String id, @RequestParam String password) {
		Map<String, Object> response = new HashMap<>();
		response.put("res", false);

		try {
			String accessToken = authService.login(id, password);

			response.put("res", true);
			response.put("accessToken", accessToken);
			response.put("refreshToken", authService.getRefreshToken(accessToken));

			return ResponseEntity.ok()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.body(response);
		}
		catch (RuntimeException re) {
			return ResponseEntity.status(401).body(re.getMessage());
		}
		catch (Exception ex) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}

	}

	@PostMapping("/refresh")
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
	public ModelAndView loginPage(Model model) {
		return new ModelAndView("member/login");
	}

	@GetMapping("/checkExistMemberId")
	// 존재하는 ID 조회
	public boolean checkExistMemberId(@RequestParam String memberId) {
		return memberService.getMemberInfoById(memberId) != null;
	}

	@PostMapping("/create")
	public boolean createMember(@RequestBody MemberDTO memberDTO) {
		try {
			if (isBlank(memberDTO.getMemberPw())) return false;
			if (isBlank(memberDTO.getMemberNm())) return false;

			return memberService.createMember(memberDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}

	@PostMapping("/update")
	// TODO test시에만 사용, 정식 배포시 MemberAllController에서는 삭제 필요
	public boolean updateMember(@RequestBody MemberDTO memberDTO) {
		try {
			return memberService.updateMemberPw(memberDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}
}
