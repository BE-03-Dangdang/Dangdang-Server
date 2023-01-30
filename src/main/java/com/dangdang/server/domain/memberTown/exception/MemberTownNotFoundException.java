package com.dangdang.server.domain.memberTown.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class MemberTownNotFoundException extends BusinessException {

  public MemberTownNotFoundException(
      ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
