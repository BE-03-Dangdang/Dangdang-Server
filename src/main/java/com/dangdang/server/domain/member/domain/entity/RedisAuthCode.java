package com.dangdang.server.domain.member.domain.entity;

import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "redisAuthCode")
public class RedisAuthCode {

  public static final Long AUTH_CODE_TTL = 600L;
  @Id
  private String id;
  @TimeToLive
  private Long expiration;

  public RedisAuthCode(String id) {
    this.id = id;
    this.expiration = AUTH_CODE_TTL;
  }
}
