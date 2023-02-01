package com.dangdang.server.domain.memberTown.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class NotAppropriateRangeException extends BusinessException {

  public NotAppropriateRangeException(
      ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
