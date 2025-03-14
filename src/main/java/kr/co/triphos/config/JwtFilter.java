package kr.co.triphos.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.member.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
//    private final JwtUtil jwtUtil;
//    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private static final List<String> EXCLUDE_URL = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-ui/",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/auth/login",
            "/test/**",
            "/auth/**"
    );

//    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // 로그인 요청에 대해서는 토큰 검증을 안함
        // 요청에 토큰이 있는 경우 로그인도 검증해서 오류가 발생
        String path = request.getRequestURI();
        if (path.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.extractMemberId(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(token)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            chain.doFilter(request, response);
        }
        catch (ExpiredJwtException e) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMsg("토큰이 만료되었습니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDTO)); // JSON으로 응답
            return;
        }
        catch (RuntimeException e) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMsg("잘못된 토큰정보입니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDTO)); // JSON으로 응답
            return;
        }
    }
}
