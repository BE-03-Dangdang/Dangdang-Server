package com.dangdang.server.domain.member.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class MemberUnmatchedAuthorException extends BusinessException {

  public MemberUnmatchedAuthorException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
