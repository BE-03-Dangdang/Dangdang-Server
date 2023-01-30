package com.dangdang.server.domain.memberTown.domain.entity;

import com.dangdang.server.domain.memberTown.exception.NotAppropriateRangeException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.Arrays;

public enum RangeType {
  LEVEL1(1),
  LEVEL2(2),
  LEVEL3(3),
  LEVEL4(4);

  private final int rangeLevel;

  RangeType(int rangeLevel) {
    this.rangeLevel = rangeLevel;
  }

  public static RangeType getRangeType(int rangeLevel) {
    return Arrays.stream(RangeType.values())
        .filter(rangeType -> rangeType.rangeLevel == rangeLevel)
        .findFirst()
        .orElseThrow(() -> new NotAppropriateRangeException(ExceptionCode.NOT_EXIST_LEVEL));
  }
}
