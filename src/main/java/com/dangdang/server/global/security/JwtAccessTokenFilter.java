package com.dangdang.server.global.security;

import com.dangdang.server.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAccessTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAccessTokenFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = jwtTokenProvider.resolveAccessToken(request);

    try {
      if (token != null && jwtTokenProvider.validateAccessToken(token)) {
        token = jwtTokenProvider.bearerRemove(token);
        Authentication authentication = jwtTokenProvider.getAccessTokenAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      response.setStatus(401);
      response.setContentType("application/json");
      response.setCharacterEncoding("utf-8");
      ErrorResponse errorResponse = ErrorResponse.from("잘못된 토큰 입니다.");
      new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }
  }
}
