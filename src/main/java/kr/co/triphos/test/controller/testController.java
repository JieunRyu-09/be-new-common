package kr.co.triphos.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Log4j2
public class testController {
	private final TestService testService;

	@GetMapping("/returnNowDate")
	@Tag(name="테스트")
	@Operation(summary = "현재날짜 반환", description = "")
	public String getTest(@RequestParam String inputText){
		try {
			Date nowdate = new Date();
			return nowdate + ": " + inputText;
		}
		catch (Exception ex) {
			return ex.getMessage();
		}

	}

	@GetMapping("/testDBTimeout")
	@Tag(name="테스트")
	@Operation(summary = "DB 커넥션 타임아웃 테스트", description = "서버 타임아웃 시간 10초.<br> 쿼리 지연시간 15초")
	public ResponseEntity<?> testDBTimeOut(){
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			boolean res = testService.testDBTimeout();
			responseDTO.setSuccess(res);
			responseDTO.setMsg("성공");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
		 catch (Exception ex) {
			 log.error(ex);
			 responseDTO.setMsg("시스템이 불안정한 상태입니다. 나중에 다시 시도하여주십시오.");
			 return ResponseEntity.internalServerError().body(responseDTO);
		 }
	}
}
