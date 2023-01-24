package com.dangdang.server.domain.pay.kftc.openBankingFacade.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class BankTypeNotFoundException extends BusinessException {

  public BankTypeNotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
