package kr.co.triphos.member.service;

import kr.co.triphos.config.JwtUtil;
import kr.co.triphos.member.Member;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * login
     * @param memberId
     * @param memberPw
     * @return String JWT token
     */
    public String login(String memberId, String memberPw) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("ID, 비밀번호를 다시 확인해 주세요."));

        // 비밀번호 확인 후 JWT 토큰 생성
        if (passwordEncoder.matches(memberPw, member.getMemberPw())) {
            return jwtUtil.generateAccessToken(memberId);
        }
        else {
            throw new RuntimeException("ID, 비밀번호를 다시 확인해 주세요.");
        }
    }

    public String getRefreshToken(String accessToken) {
        return jwtUtil.generateRefreshToken(jwtUtil.extractMemberId(accessToken));
    }

    public String refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        String username = jwtUtil.extractMemberId(refreshToken);
        return jwtUtil.generateAccessToken(username);
    }

}
