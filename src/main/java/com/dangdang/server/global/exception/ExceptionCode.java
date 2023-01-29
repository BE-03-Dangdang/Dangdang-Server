package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 카테고리가 존재하지 않습니다."),
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증에 실패히였습니다"),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "타운을 찾지 못하였습니다"),
  BINDING_WRONG(HttpStatus.BAD_REQUEST.value(), "요청하신 필드값의 유효성이 잘못되었습니다."),
  IMAGE_URL_INVALID(HttpStatus.BAD_REQUEST.value(), "이미지 주소가 잘못되었습니다."),
  POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 게시글이 존재하지 않습니다."),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버를 찾지 못하였습니다"),
  MEMBER_TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 타운을 찾지 못하였습니다"),
  NOT_APPROPRIATE_COUNT(HttpStatus.BAD_REQUEST.value(), "허용 가능한 갯수가 아닙니다"),
  NOT_PERMISSION(HttpStatus.FORBIDDEN.value(), "권한이 없습니다"),
  NOT_EXIST_LEVEL(HttpStatus.BAD_REQUEST.value(), "레벨이 잘못된 범위입니다"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "요청이 잘못되었습니다."),
  // pay
  TRUST_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "신탁 계좌를 찾지 못했습니다."),
  TRUST_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 신탁계좌입니다."),
  BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "은행 계좌를 찾지 못했습니다."),
  BANK_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST.value(), "사용할 수 없는 은행계좌입니다."),
  BANK_ACCOUNT_AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST.value(), "은행계좌의 인증에 실패했습니다."),
  PAY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "당근페이 유저를 찾지 못했습니다."),
  CHARGE_LESS_THAN_MIN_AMOUNT(HttpStatus.BAD_REQUEST.value(), "최소 충전금액은 10,000원입니다."),
  WITHDRAW_LESS_THAN_MIN_AMOUNT(HttpStatus.BAD_REQUEST.value(), "최소 출금금액은 1원입니다."),
  INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST.value(), "계좌의 잔액이 부족합니다."),
  BANK_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "금융기관을 찾지 못했습니다."), ;

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
