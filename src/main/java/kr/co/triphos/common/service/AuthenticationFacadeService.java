package kr.co.triphos.common.service;

import kr.co.triphos.member.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFacadeService {
	// 로그인한 사용자의 security 인증객체 가져오기
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	// 로그인한 사용자의 memberId조회
	public String getMemberId() {
		Object principal = getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) { // 본인 UserDetails 구현체
			CustomUserDetails customUserDetails = (CustomUserDetails) principal;
			return customUserDetails.getMemberId();
		}
		return null;
	}

	// 로그인한 사용자의 memberId조회
	public String getMemberNm() {
		Object principal = getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) { // 본인 UserDetails 구현체
			CustomUserDetails customUserDetails = (CustomUserDetails) principal;
			return customUserDetails.getMemberNm();
		}
		return null;
	}
}
