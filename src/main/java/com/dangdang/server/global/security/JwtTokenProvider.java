package com.dangdang.server.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider  {

  private final String accessTokenSecretKey;
  private final String refreshTokenSecretKey;

  private final long ACCESS_TOKEN_EXPIRED_TIME = Duration.ofMinutes(5).toMillis(); // 만료시간 5분
  private final long REFRESH_TOKEN_EXPIRED_TIME = Duration.ofMinutes(60).toMillis();

  private final CustomUserDetailsService customUserDetailsService;

  public JwtTokenProvider(@Value("${spring.jwt.AccessTokenSecretKey}")
  String accessTokenSecretKey,
      @Value("${spring.jwt.RefreshTokenSecretKey}") String refreshTokenSecretKey,
      CustomUserDetailsService customUserDetailsService) {
    this.accessTokenSecretKey = Base64
        .getEncoder()
        .encodeToString(accessTokenSecretKey.getBytes());
    this.refreshTokenSecretKey = Base64
        .getEncoder()
        .encodeToString(refreshTokenSecretKey.getBytes());
    this.customUserDetailsService = customUserDetailsService;
  }

  public String createAccessToken(long memberId) {
    return getToken(memberId, ACCESS_TOKEN_EXPIRED_TIME, accessTokenSecretKey);
  }

  public String createRefreshToken(Long memberId) {
    return getToken(memberId, REFRESH_TOKEN_EXPIRED_TIME, refreshTokenSecretKey);
  }
  
  public Authentication getAccessTokenAuthentication(String token) {
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(getMemberId(token, accessTokenSecretKey));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public Authentication getRefreshTokenAuthentication(String token) {
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(getMemberId(token, refreshTokenSecretKey));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getMemberId(String token, String secretKey) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
        .get("memberId").toString();
  }

  public String resolveAccessToken(HttpServletRequest request) {
    return request.getHeader("AccessToken");
  }

  public String resolveRefreshToken(HttpServletRequest request) {
    return request.getHeader("RefreshToken");
  }

  public boolean validateAccessToken(String token) {
    return extracted(token, accessTokenSecretKey);
  }

  public boolean validateRefreshToken(String token) {
    return extracted(token, refreshTokenSecretKey);
  }

  public String bearerRemove(String token) {
    return token.substring("Bearer ".length());
  }

  private String getToken(long memberId, long ACCESS_TOKEN_EXPIRED_TIME, String secretKey) {
    Claims claims = Jwts.claims().setSubject("Dangdang");
    claims.put("memberId", memberId);
    Date date = new Date();

    return Jwts.builder()
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setClaims(claims)
        .setIssuedAt(date)
        .setIssuer("Dangdang-server")
        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRED_TIME))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  private boolean extracted(String token, String accessTokenSecretKey) {
    token = bearerRemove(token);
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(accessTokenSecretKey).parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}
