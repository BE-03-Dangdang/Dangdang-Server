package com.dangdang.server.domain.member.domain.entity;

import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "redisSendSms")
public class RedisSendSms {

  @Id
  private String id;

  public RedisSendSms(String phoneNumber) {
    this.id = phoneNumber;
  }
}
