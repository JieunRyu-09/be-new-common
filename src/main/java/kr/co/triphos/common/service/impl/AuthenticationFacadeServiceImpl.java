package kr.co.triphos.common.service.impl;

import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFacadeServiceImpl implements AuthenticationFacadeService {
	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public String getMemberId() {
		Object principal = getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) { // 본인 UserDetails 구현체
			CustomUserDetails customUserDetails = (CustomUserDetails) principal;
			return customUserDetails.getMemberId();
		}
		return null;
	}

	@Override
	public String getMemberNm() {
		Object principal = getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) { // 본인 UserDetails 구현체
			CustomUserDetails customUserDetails = (CustomUserDetails) principal;
			return customUserDetails.getMemberNm();
		}
		return null;
	}

	@Override
	public String getMemberType() {
		Object principal = getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) { // 본인 UserDetails 구현체
			CustomUserDetails customUserDetails = (CustomUserDetails) principal;
			return customUserDetails.getMemberType();
		}
		return null;
	}
}
