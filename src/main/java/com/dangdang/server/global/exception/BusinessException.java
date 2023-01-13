package com.dangdang.server.global.exception;

public class BusinessException extends RuntimeException {

  private final int status;

  public BusinessException(ExceptionCode exceptionCode) {
    super(exceptionCode.getMessage());
    this.status = exceptionCode.getStatus();
  }

  public int getStatus() {
    return status;
  }
}
