package kr.co.triphos.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.dto.CustomUserDetails;
import kr.co.triphos.member.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Lazy
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;      // at least 32 characters long
    private Key key;
    /** 로컬테스트랑 서버랑 토큰시간 별도로 관리
     *  서버는 일정시간마다 access토큰 유효시간 체크
     */
    @Value("${token.time}")
    private long ACCESS_TOKEN_VALIDITY;
    private long TEMP_TOKEN_VALIDITY = 1000 * 30;
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일
    private final String HEADER_STRING = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private final RedisService redisService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    // Access Token 생성
    public HashMap<String, String> generateAccessToken(String memberID) {
        HashMap<String, String> resultMap = new HashMap<>();
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String accessToken = Jwts.builder()
                .setSubject(memberID)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        resultMap.put("accessToken", accessToken);
        resultMap.put("expirationDate", sdf.format(expirationDate));
        return resultMap;
    }

    // 임시 Access Token 생성
    public HashMap<String, String> generateTempAccessToken(String memberID) {
        HashMap<String, String> resultMap = new HashMap<>();
        Date expirationDate = new Date(System.currentTimeMillis() + TEMP_TOKEN_VALIDITY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String accessToken = Jwts.builder()
                .setSubject(memberID)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        resultMap.put("accessToken", accessToken);
        resultMap.put("expirationDate", sdf.format(expirationDate));
        return resultMap;
    }

    // Refresh Token 생성
    public String generateRefreshToken(String memberID) {
        return Jwts.builder()
                .setSubject(memberID)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

    public boolean checkToken(String token) throws Exception {
        String username = extractMemberId(token);
        // redis의 토큰정보와 비교
        // 중복로그인 방지.
        Map<Object, Object> redisTokenMap = redisService.getMapData(username);
        if (redisTokenMap.isEmpty()) throw new RuntimeException("로그인 정보가 만료되었습니다.");
        String redisAccessToken = redisTokenMap.get("accessToken").toString();
        if (!token.equals(redisAccessToken)) throw new RuntimeException("다른곳에서 로그인하여 로그인정보가 만료되었습니다.");

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        return true;
    }

    public String extractMemberId(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        return extractMemberId(token) != null && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public Date getTokenExpireTime(String token) {return getClaims(token).getExpiration();}

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
