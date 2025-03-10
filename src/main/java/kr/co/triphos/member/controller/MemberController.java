package kr.co.triphos.member.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.service.ResponseDTO;
import kr.co.triphos.member.dto.memberDTO.MemberDTO;
import kr.co.triphos.member.dto.memberDTO.MemberUpdateDTO;
import kr.co.triphos.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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
	@Operation(summary = "사용자정보 수정", description = "해당 api는 회원가입 및 로그인 등 구현 완료 후 삭제 예정")
	public ResponseEntity<?> updateMember(@RequestBody MemberUpdateDTO memberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			boolean res = memberService.updateMemberPw(memberDTO);
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

}
