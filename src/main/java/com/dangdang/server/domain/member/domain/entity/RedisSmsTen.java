package com.dangdang.server.domain.member.domain.entity;

import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "redisSmsTen")
public class RedisSmsTen {

  public static final Long SEND_SMS_TTL = 10L;

  @Id
  private String id;
  @TimeToLive
  private Long expiration;

  public RedisSmsTen(String id) {
    this.id = id;
    this.expiration = SEND_SMS_TTL;
  }
}
