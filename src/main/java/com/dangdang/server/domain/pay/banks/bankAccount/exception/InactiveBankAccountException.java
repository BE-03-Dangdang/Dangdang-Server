package com.dangdang.server.domain.pay.banks.bankAccount.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class InactiveBankAccountException extends BusinessException {

  public InactiveBankAccountException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
