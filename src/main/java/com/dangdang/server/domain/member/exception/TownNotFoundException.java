package com.dangdang.server.domain.member.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class TownNotFoundException extends BusinessException {

  public TownNotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
