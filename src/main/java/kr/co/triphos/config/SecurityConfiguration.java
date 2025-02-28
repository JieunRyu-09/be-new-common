package kr.co.triphos.config;

import kr.co.triphos.member.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	private final CustomUserDetailsService customUserDetailsService;
	public SecurityConfiguration(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement(session -> session
					.maximumSessions(-1) // 세션 무제한
					//.maximumSessions(1).maxSessionsPreventsLogin(true) // 새로운 로그인 차단 (기존 세션 유지)
			)
			.authorizeHttpRequests(auth -> auth
					.antMatchers("/test/**").permitAll()  // test 경로는 인증 불필요
					.antMatchers("/member/all/**").permitAll()  // test 경로는 인증 불필요
					.anyRequest().authenticated() 			// 나머지 요청은 인증 필요
			)
			.formLogin(login -> login
					//.loginPage("/member/all/loginPage").permitAll() // 로그인 페이지 별도 설정 시 필요
					.loginProcessingUrl("/login").permitAll()
					.defaultSuccessUrl("/member/auth/loginSuccess", true) // 로그인 성공 시 이동할 페이지
			)
			.logout(logout -> logout
					.logoutSuccessUrl("/login")
					.permitAll()
			)
			.userDetailsService(customUserDetailsService); // 사용자 정보 제공

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
