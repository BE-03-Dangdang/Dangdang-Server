package com.dangdang.server.domain.post.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class NullParameterException extends BusinessException {

  public NullParameterException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
