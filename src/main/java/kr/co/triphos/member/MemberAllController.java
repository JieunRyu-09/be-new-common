package kr.co.triphos.member;

import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static kr.co.triphos.common.service.CommonFunc.*;


@RestController
@RequestMapping("/member/all")
@RequiredArgsConstructor
@Log4j2
@EnableWebMvc
public class MemberAllController {
	private final MemberService memberService;

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
