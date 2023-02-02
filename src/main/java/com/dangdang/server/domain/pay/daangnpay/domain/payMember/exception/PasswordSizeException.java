package com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class PasswordSizeException extends BusinessException {

  public PasswordSizeException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
