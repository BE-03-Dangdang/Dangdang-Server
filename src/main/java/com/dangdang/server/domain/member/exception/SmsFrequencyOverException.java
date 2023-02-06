package com.dangdang.server.domain.member.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class SmsFrequencyOverException extends BusinessException {

  public SmsFrequencyOverException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
