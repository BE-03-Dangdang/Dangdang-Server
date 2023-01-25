package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증에 실패히였습니다"),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"타운을 찾지 못하였습니다"),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버를 찾지 못하였습니다"),
  MEMBER_TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 타운을 찾지 못하였습니다"),
  NOT_APPROPRIATE_COUNT(HttpStatus.BAD_REQUEST.value(), "허용 가능한 갯수가 아닙니다"),
  NOT_PERMISSION(HttpStatus.FORBIDDEN.value(), "권한이 없습니다"),
  NOT_EXIST_LEVEL(HttpStatus.BAD_REQUEST.value(), "레벨이 잘못된 범위입니다"),

  // pay
  BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "은행 계좌를 찾지 못했습니다."),
  BANK_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 계좌입니다."),
  PAY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "당근페이 유저를 찾지 못했습니다.");

  private final int status;
  private final String message;

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
