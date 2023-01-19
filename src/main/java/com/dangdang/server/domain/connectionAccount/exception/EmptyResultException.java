package com.dangdang.server.domain.connectionAccount.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class EmptyResultException extends BusinessException {

  public EmptyResultException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
