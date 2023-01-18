package com.dangdang.server.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

  private String secretKey;

  private final CustomUserDetailsService customUserDetailsService;

  public JwtTokenProvider(@Value("${spring.jwt.secretKey}")
  String secretKey, CustomUserDetailsService customUserDetailsService) {
    this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    this.customUserDetailsService = customUserDetailsService;
  }


  public String createAccessToken(long memberId) {
    Claims claims = Jwts.claims().setSubject("Dangdang");
    claims.put("memberId", memberId);

    Date date = new Date();

    long expiredTime = 1000 * 60 * 30L;
    return Jwts.builder()
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setClaims(claims)
        .setIssuedAt(date)
        .setIssuer("Dangdang-server")
        .setExpiration(new Date(date.getTime() + expiredTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  //토큰에서 인증정보를 조회하는 메서드
  public Authentication getAuthentication(String token) {
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(getMemberId(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getMemberId(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
        .get("memberId").toString();
  }

  public String resolveAccessToken(HttpServletRequest request) {
    return request.getHeader("AccessToken");
  }

  public boolean validateAccessToken(String token) {
    token = bearerRemove(token);
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public String bearerRemove(String token) {
    return token.substring("Bearer ".length());
  }

}
