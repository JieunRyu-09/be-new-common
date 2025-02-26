package kr.co.triphos.test.controller;

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
	public String getTest(@RequestParam String inputText){
		Date nowdate = new Date();
		return nowdate + ": " + inputText;
	}
}
