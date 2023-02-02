package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-pay.properties")
@Getter
public class AuthTokenRequestProperties {

  @Value("${auth-token-req.id}")
  private String id;
  @Value("${auth-token-req.secret}")
  private String secret;
  @Value("${auth-token-req.uri}")
  private String uri;
  @Value("${auth-token-req.grant-type}")
  private String grantType;

}
