package com.dangdang.server.domain.member.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class SmsRequestException extends BusinessException {

  public SmsRequestException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
