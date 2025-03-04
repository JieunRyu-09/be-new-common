package kr.co.triphos.member.controller;

import kr.co.triphos.member.MemberDTO;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@RequestMapping("/member/auth")
@RequiredArgsConstructor
@Log4j2
@EnableWebMvc
public class MemberAuthController {
	private final MemberService memberService;

	@GetMapping("/loginSuccess")
	public ModelAndView loginSuccessPage(Model model) {
		return new ModelAndView("main");
	}

	@PostMapping("/update")
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
