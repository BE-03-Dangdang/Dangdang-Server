package com.dangdang.server.domain.memberTown.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class NotAppropriateCountException extends BusinessException {
  public NotAppropriateCountException(
      ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
