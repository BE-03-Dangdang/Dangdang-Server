package com.dangdang.server.domain.post.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class MinParameterException extends BusinessException {

  public MinParameterException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
