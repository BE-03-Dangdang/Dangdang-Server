package com.dangdang.server.global.exception;

public enum ExceptionCode {
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
