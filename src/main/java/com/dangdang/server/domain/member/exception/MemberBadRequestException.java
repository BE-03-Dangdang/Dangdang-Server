package com.dangdang.server.domain.member.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class MemberBadRequestException extends BusinessException {

  public MemberBadRequestException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
