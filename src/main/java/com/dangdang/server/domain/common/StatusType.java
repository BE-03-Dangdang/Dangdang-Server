package com.dangdang.server.domain.common;

import static com.dangdang.server.global.exception.ExceptionCode.STATUS_TYPE_MISMATCH;

import com.dangdang.server.domain.common.exception.InvalidStatusTypeException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusType {

  ACTIVE, INACTIVE,
  RESERVED, SELLING, COMPLETED; //Post 상태값

  @JsonCreator
  public static StatusType getEnumFromValue(String value) {
    try {
      return StatusType.valueOf(value.toUpperCase());
    } catch(Exception e) {
      throw new InvalidStatusTypeException(STATUS_TYPE_MISMATCH);
    }
  }
}
