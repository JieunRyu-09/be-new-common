package kr.co.triphos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 인증과 권한 설정
		configureAuthorization(http);
		// 로그인 설정
		configureLogin(http);
		// 로그아웃 설정
		configureLogout(http);
	}

	private void configureAuthorization(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/test/**").permitAll()  // test 경로는 인증 불필요
				.anyRequest().authenticated(); 			// 나머지 요청은 인증 필요
	}

	private void configureLogin(HttpSecurity http) throws Exception {
		http.formLogin()  // 기본 로그인 폼 사용
				.loginPage("/login")  // 로그인 페이지 설정
				.permitAll();
	}

	private void configureLogout(HttpSecurity http) throws Exception {
		http.logout()  // 로그아웃 설정
				.permitAll();
	}
}
