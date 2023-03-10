package com.dangdang.server.global.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BindException.class)
  protected ResponseEntity<String> handleBindException(BindException e) {
    int httpStatus = ExceptionCode.BINDING_WRONG.getStatus();
    String message = ExceptionCode.BINDING_WRONG.getMessage();
    return ResponseEntity.status(httpStatus).body(message);
  }

  @ExceptionHandler(BusinessException.class)
  protected ResponseEntity<String> handledException(BusinessException e) {
    log.info(e.getMessage(), e);
    HttpStatus httpStatus = HttpStatus.valueOf(e.getStatus());
    return ResponseEntity.status(httpStatus).body(e.getMessage());
  }
}
