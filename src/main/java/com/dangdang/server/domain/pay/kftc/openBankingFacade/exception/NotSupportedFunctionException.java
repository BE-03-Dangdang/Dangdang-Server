package com.dangdang.server.domain.pay.kftc.openBankingFacade.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class NotSupportedFunctionException extends BusinessException {

  public NotSupportedFunctionException(
      ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
