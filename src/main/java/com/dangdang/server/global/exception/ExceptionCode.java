package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증 실패")
  ;

  int status;
  String message;

  ExceptionCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
