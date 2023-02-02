package com.dangdang.server.domain.post.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class InvalidParameterException extends BusinessException {

  public InvalidParameterException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
