package com.dangdang.server.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  protected ResponseEntity<String> handledException(BusinessException e) {
    log.info(e.getMessage(), e);
    HttpStatus httpStatus = HttpStatus.valueOf(e.getStatus());
    return ResponseEntity.status(httpStatus).body(e.getMessage());
  }



}
