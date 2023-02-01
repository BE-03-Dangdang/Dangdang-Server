package com.dangdang.server.domain.pay.banks.trustAccount.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class InsufficientTrustAccountException extends BusinessException {


  public InsufficientTrustAccountException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
