package com.dangdang.server.domain.post.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class PostNotFoundException extends BusinessException {

  public PostNotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
