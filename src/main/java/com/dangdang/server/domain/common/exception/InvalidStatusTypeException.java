package com.dangdang.server.domain.common.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class InvalidStatusTypeException extends BusinessException {

  public InvalidStatusTypeException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
