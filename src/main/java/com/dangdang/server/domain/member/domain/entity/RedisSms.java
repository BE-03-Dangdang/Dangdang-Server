package com.dangdang.server.domain.member.domain.entity;

import com.dangdang.server.domain.member.exception.MemberCertifiedFailException;
import com.dangdang.server.global.exception.ExceptionCode;
import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "redisSms")
public class RedisSms {

  public static final Long SMS_TTL = 300L;

  @Id
  private String id;
  @Indexed
  private String authCode;
  @TimeToLive
  private Long expiration;

  public RedisSms(String id, String authCode) {
    this.id = id;
    this.authCode = authCode;
    this.expiration = SMS_TTL;
  }

  public void validateAuthCode(String authCode) {
    if (!this.authCode.equals(authCode)) {
      throw new MemberCertifiedFailException(ExceptionCode.CERTIFIED_FAIL);
    }
  }
}
