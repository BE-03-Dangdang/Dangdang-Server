package com.dangdang.server.global.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 카테고리가 존재하지 않습니다."),
  TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 도시가 존재하지 않습니다."),
  CERTIFIED_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증에 실패히였습니다"),
  BINDING_WRONG(HttpStatus.BAD_REQUEST.value(), "요청하신 필드값의 유효성이 잘못되었습니다."),
  IMAGE_URL_INVALID(HttpStatus.BAD_REQUEST.value(), "이미지 주소가 잘못되었습니다."),
  POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 게시글이 존재하지 않습니다."),
  OVER_COUNT(HttpStatus.METHOD_NOT_ALLOWED.value(), "허용 가능한 갯수가 아닙니다!"),
  NOT_PERMISSION(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),

  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버를 찾지 못하였습니다"),
  MEMBER_TOWN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "멤버 타운을 찾지 못하였습니다"),
  NOT_APPROPRIATE_COUNT(HttpStatus.BAD_REQUEST.value(), "허용 가능한 갯수가 아닙니다"),
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
  BANK_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "금융기관을 찾지 못했습니다."),
  MEMBER_UNMATCH_AUTHOR(HttpStatus.FORBIDDEN.value(), "글 작성자가 아닙니다."),
  OPEN_OAUTH_NOT_COMPLETE(HttpStatus.BAD_REQUEST.value(), "계좌등록 및 인증 처리 여부를 확인해주세요."),
  NOT_SUPPORTED(HttpStatus.BAD_REQUEST.value(), "현재 환경에서 지원하지 않는 기능입니다. 외부 연동 환경에서 요청해주세요."),

  // post
  POST_STATUS_IS_NULL(HttpStatus.BAD_REQUEST.value(), "글 상태값은 비어있을 수 없습니다."),
  SLICE_PARAMETER_UNDER_ZERO(HttpStatus.BAD_REQUEST.value(), "글 페이지, 사이즈는 음수일 수 없습니다."),
  STATUS_TYPE_MISMATCH(HttpStatus.BAD_REQUEST.value(), "일치하는 상태값이 없습니다."),
  INVALID_POST_STATUS(HttpStatus.BAD_REQUEST.value(), "글 상태값에 적절한 값이 아닙니다."),
  SEARCH_KEYWORD_MUST_EXIST(HttpStatus.BAD_REQUEST.value(), "검색어는 필수 입력 항목입니다."),
  UPDATABLE_POST_NOT_EXIST(HttpStatus.NO_CONTENT.value(), "변경할 데이터가 없습니다."),

  // token
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효한 토큰 값이 아닙니다"),

  // membertown
  NO_ACTIVE_TOWN(HttpStatus.INTERNAL_SERVER_ERROR.value(), "활성화된 동네가 없습니다."),

  // review
  REVIEW_WRONG_ACCESS(HttpStatus.BAD_REQUEST.value(), "거래가 완료된 후에만 리뷰 작성이 가능합니다."),

  // chat
  CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "채팅방을 찾지 못하였습니다");

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
