package kr.co.triphos.member.controller;

import kr.co.triphos.member.Member;
import kr.co.triphos.member.MemberDTO;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
	private final MemberService memberService;

	@GetMapping("/all/loginPage")
	public ModelAndView loginPage(Model model) {
		return new ModelAndView("member/login");
	}

	@GetMapping("/auth/loginSuccess")
	public ModelAndView loginSuccessPage(Model model) {
		return new ModelAndView("main");
	}


	@GetMapping("/all/checkExistMemberId")
	// 존재하는 ID 조회
	public boolean checkExistMemberId(@RequestParam String memberId) {
		return memberService.getMemberInfoById(memberId) != null;
	}

	@PostMapping("/all/create")
	public boolean createMember(@RequestBody Member member) {
		try {
			return memberService.createMember(member);
		}
		catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}

	@PostMapping("/all/update")
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
