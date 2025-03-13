package kr.co.triphos.common.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacadeService {
	// 로그인한 사용자의 security 인증객체 가져오기
	Authentication getAuthentication();
	// 로그인한 사용자의 memberId조회
	String getMemberId();
}
