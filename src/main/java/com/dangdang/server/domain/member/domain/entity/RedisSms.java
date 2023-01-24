package com.dangdang.server.domain.member.domain.entity;

import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "redisSms")
public class RedisSms {

  @Id
  private String id;
  @Indexed
  private String phoneNumber;
  @Indexed
  private String authCode;
  @TimeToLive
  private Long expiration;

  public RedisSms(String phoneNumber, String authCode) {
    this.id = phoneNumber;
    this.phoneNumber = phoneNumber;
    this.authCode = authCode;
    this.expiration = 300L;
  }
}
