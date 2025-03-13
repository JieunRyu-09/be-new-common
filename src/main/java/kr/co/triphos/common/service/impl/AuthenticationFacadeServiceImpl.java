package kr.co.triphos.common.service.impl;

import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.CustomUserDetailsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationFacadeServiceImpl implements AuthenticationFacadeService {
	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public String getUserId() {
		Object principal = getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetailsDTO) { // 본인 UserDetails 구현체
			return ((CustomUserDetailsDTO) principal).getUsername();
		}
		return null;
	}
}
