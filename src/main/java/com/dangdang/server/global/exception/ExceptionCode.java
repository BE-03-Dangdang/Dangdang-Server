package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

  // pay
  BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "은행 계좌를 찾지 못했습니다."),
  BANK_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 계좌입니다."),
  PAY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "당근페이 유저를 찾지 못했습니다.");

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
