package kr.co.triphos.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@Log4j2
public class common {

	@GetMapping("/test")
	public String getTest(@RequestParam String inputText){
		Date nowdate = new Date();
		return nowdate + ": " + inputText;
	}
}
