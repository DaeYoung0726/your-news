package com.project.tableforyou.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {              // 토큰 유효성 검증 실패 시 처리
            handleExceptionToken(response, ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    /* 예외 처리 */
    private void handleExceptionToken(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        ErrorDto error = new ErrorDto(errorCode.getStatus(), errorCode.getMessage());
        String messageBody = objectMapper.writeValueAsString(error);

        log.error("Error occurred: {}", error.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(messageBody);
    }
}