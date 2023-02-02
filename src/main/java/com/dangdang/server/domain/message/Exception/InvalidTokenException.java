package com.dangdang.server.domain.message.Exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class InvalidTokenException extends BusinessException {

  public InvalidTokenException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
