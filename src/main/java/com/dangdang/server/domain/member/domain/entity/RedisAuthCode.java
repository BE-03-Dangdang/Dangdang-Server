package com.dangdang.server.domain.member.domain.entity;

import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "redisAuthCode")
public class RedisAuthCode {

  public static final Long AUTH_CODE_TTL= 600L;
  @Id
  private String id;
  @Indexed
  private String phoneNumber;
  @Indexed
  private Boolean authCheck;
  @TimeToLive
  private Long expiration;

  public RedisAuthCode(String phoneNumber, Boolean authCheck) {
    this.id = phoneNumber;
    this.phoneNumber = phoneNumber;
    this.authCheck = authCheck;
    this.expiration = AUTH_CODE_TTL;
  }
}
