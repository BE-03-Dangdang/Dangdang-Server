package com.dangdang.server.global.dto;

public record ErrorResponse (
    String message
) {
  public static ErrorResponse from(String message) {
    return new ErrorResponse(message);
  }
}
