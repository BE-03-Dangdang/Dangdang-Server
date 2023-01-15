package com.dangdang.server.domain.member.service;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.utill.RedisUtil;
import org.springframework.stereotype.Service;

@Service
public class MemberCertifiedService {

  private final RedisUtil redisUtil;

  public MemberCertifiedService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  public void certify(String toNumber, String randomNumber) {
    String code = redisUtil.getData(toNumber);

    if (!code.equals(randomNumber)) {
      throw new BusinessException(ExceptionCode.CERTIFIED_FAIL);
    }
  }
}
