package com.dangdang.server.domain.pay.banks.bankAccount.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class BankAccountAuthenticationException extends BusinessException {

  public BankAccountAuthenticationException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
