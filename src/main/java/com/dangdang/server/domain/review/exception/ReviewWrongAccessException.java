package com.dangdang.server.domain.review.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class ReviewWrongAccessException extends BusinessException {

  public ReviewWrongAccessException(
      ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
