package com.dangdang.server.domain.post.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class CategoryNotFoundException extends BusinessException {

  public CategoryNotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
