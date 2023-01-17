package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증 실패"),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"타운 찾지 못함"),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 찾지 못함")
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
