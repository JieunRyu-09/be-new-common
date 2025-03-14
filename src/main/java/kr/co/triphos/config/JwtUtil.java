package kr.co.triphos.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Component
@Lazy
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;      // at least 32 characters long
    private Key key;
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 1시간
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일

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

    public String extractMemberId(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        return extractMemberId(token) != null && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
