package kr.co.triphos.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
@EnableWebMvc
public class MemberController {
	private final PasswordEncoder passwordEncoder;
	private final MemberService memberService;

	@GetMapping("/all/loginPage")
	public ModelAndView loginPage(Model model) {
		return new ModelAndView("member/login");
	}

	@GetMapping("/auth/loginSuccess")
	public ModelAndView loginSuccessPage(Model model) {
		return new ModelAndView("main");
	}

	@PostMapping("/all/login")
	public String login(@RequestParam String memberId, @RequestParam String memberPw) {
		String resultMsg = null;
		try {
			log.info("[ID: " + memberId + "] [PW: " + memberPw + "]");
			if (memberId.isEmpty()) throw new Exception("아이디를 입력하세요.");
			if (memberPw.isEmpty()) throw new Exception("비밀번호를 입력하세요.");

			resultMsg = "LOGIN SUCCESS";
		}
		catch (Exception ex) {
			log.error(ex);
			resultMsg = ex.toString();
		}
		return resultMsg;
	}

	@GetMapping("/all/checkExistMemberId")
	// 존재하는 ID 조회
	public boolean checkExistMemberId(@RequestParam String memberId) {
		Member member = memberService.getMemberInfoById(memberId);
		return member != null;
	}

	@PostMapping("/all/create")
	public String createMember(@RequestBody Member member) {
		String resultMsg = null;
		try {
			String memberId = member.getMemberId();
			String memberPw = member.getMemberPw();
			Member existMemeber = memberService.getMemberInfoById(memberId);
			if (existMemeber != null) {
				return "이미 등록된 사용자입니다.";
			}
			String encodedPassword = passwordEncoder.encode(member.getMemberPw());
			log.info("scardy enco" + encodedPassword);
			member.setMemberPw(encodedPassword);
			Member newMember = memberService.createMember(member);

			resultMsg = "CREATE SUCCESS";

		}
		catch (Exception ex) {
			log.error(ex);
			resultMsg = ex.toString();
		}
		return resultMsg;
	}



}
