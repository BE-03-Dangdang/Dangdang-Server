package com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class MinAmountException extends BusinessException {

  public MinAmountException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
