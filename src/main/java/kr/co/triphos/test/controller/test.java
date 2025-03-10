package kr.co.triphos.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Log4j2
public class test {

	@GetMapping("/returnNowDate")
	@Tag(name="테스트")
	@Operation(summary = "현재날짜 반환", description = "")
	public String getTest(@RequestParam String inputText){
		Date nowdate = new Date();
		return nowdate + ": " + inputText;
	}
}
