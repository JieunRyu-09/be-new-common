package kr.co.triphos.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // 해당 경로는 토큰검증 X
        String path = request.getRequestURI();
        if (path.equals("/auth/login") || path.startsWith("/ws")) {
            chain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            boolean result = jwtUtil.checkToken(token);

            if (result) {
                chain.doFilter(request, response);
            }
            else {
                throw new RuntimeException("사용자정보 확인에 실패하였습니다.");
            }
        }
        catch (DataAccessException ex) {
            log.error(ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_REQUEST_TIMEOUT, "서버현황이 불안정합니다. 잠시 후 다시 시도하여주십시오.");
            return;
        }
        catch (ExpiredJwtException ex) {
            log.error(ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
            return;
        }
        catch (RuntimeException ex) {
            log.error(ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return;
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버에 이상이 발생하였습니다. 잠시후 시도하여주십시오.");
            return;
        }

    }


    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMsg(message);

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseDTO));
    }
}
