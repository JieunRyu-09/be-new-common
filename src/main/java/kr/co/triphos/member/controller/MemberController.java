package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.member.dto.memberDTO.MemberDTO;
import kr.co.triphos.member.dto.memberDTO.MemberUpdateDTO;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/update")
	@Tag(name="사용자 관리")
	@Operation(summary = "사용자정보 수정", description = "")
	public boolean updateMember(@RequestBody MemberUpdateDTO memberDTO) {
		try {
			return memberService.updateMemberPw(memberDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}

}
