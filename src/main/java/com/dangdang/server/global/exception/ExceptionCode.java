package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증 실패"),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"타운 찾지 못함"),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 찾지 못함"),
  MEMBER_TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 타운을 찾지 못함"),
  NOT_APPROPRIATE_COUNT(HttpStatus.METHOD_NOT_ALLOWED.value(), "허용 가능한 갯수가 아닙니다!"),
  NOT_PERMISSION(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),
  NOT_EXIST_LEVEL(HttpStatus.BAD_REQUEST.value(), "잘못된 범위입니다")
  ;

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
