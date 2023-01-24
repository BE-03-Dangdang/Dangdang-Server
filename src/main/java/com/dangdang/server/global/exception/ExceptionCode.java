package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증 실패"),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"타운 찾지 못함"),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 찾지 못함"),
  MEMBER_TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 타운을 찾지 못함"),
  OVER_COUNT(HttpStatus.METHOD_NOT_ALLOWED.value(), "허용 가능한 갯수가 아닙니다!"),
  NOT_PERMISSION(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),

  // pay
  TRUST_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "신탁 계좌를 찾지 못했습니다."),
  TRUST_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 신탁계좌입니다."),
  BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "은행 계좌를 찾지 못했습니다."),
  BANK_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 은행계좌입니다."),
  BANK_ACCOUNT_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED.value(), "은행계좌의 인증에 실패했습니다."),
  PAY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "당근페이 유저를 찾지 못했습니다."),
  LESS_THAN_MIN_AMOUNT(HttpStatus.BAD_REQUEST.value(), "최소 충전금액은 10,000원입니다."),
  INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST.value(), "계좌의 잔액이 부족합니다."),
  BANK_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "금융기관을 찾지 못했습니다.");

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
