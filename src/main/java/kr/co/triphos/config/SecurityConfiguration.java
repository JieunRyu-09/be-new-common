package kr.co.triphos.config;

import kr.co.triphos.member.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
					.antMatchers(
							"/",
							"/swagger-ui.html",
							"/swagger-ui/**",
							"/v3/api-docs/**",  // 보통 기본 경로지만 현재는 '/api-docs'로 바꾸셨으니
							"/api-docs/**"
					).permitAll()  // swagger 경로 모두 허용
					.antMatchers("/test/**").permitAll()  		// test 경로는 인증 불필요
					.antMatchers("/auth/**").permitAll()  		// auth 경로는 인증 불필요(비로그인시 사용)
					.anyRequest().authenticated() 					// 나머지 요청은 인증 필요
			)
//			.formLogin(login -> login
//					//.loginPage("/member/all/loginPage").permitAll() // 로그인 페이지 별도 설정 시 필요
//					.loginProcessingUrl("/login").permitAll()
//					.defaultSuccessUrl("/member/auth/loginSuccess", true) // 로그인 성공 시 이동할 페이지
//			)
			.logout(logout -> logout
					.logoutSuccessUrl("/login")
					.permitAll()
			)
			.userDetailsService(customUserDetailsService) // 사용자 정보 제공
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
