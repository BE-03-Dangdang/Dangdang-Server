package com.dangdang.server.domain.member.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class MemberNotFoundException extends BusinessException {

  public MemberNotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
