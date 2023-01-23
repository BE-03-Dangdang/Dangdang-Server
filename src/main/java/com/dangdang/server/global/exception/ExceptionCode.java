package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 카테고리가 존재하지 않습니다."),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 도시가 존재하지 않습니다."),
  POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 게시글이 존재하지 않습니다."),
  BINDING_WRONG(HttpStatus.BAD_REQUEST.value(), "요청하신 필드값의 유효성이 잘못되었습니다."),
  IMAGE_URL_INVALID(HttpStatus.BAD_REQUEST.value(), "이미지 주소가 잘못되었습니다.");

  BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "은행 계좌를 찾지 못했습니다."),
  BANK_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 계좌입니다."),
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
