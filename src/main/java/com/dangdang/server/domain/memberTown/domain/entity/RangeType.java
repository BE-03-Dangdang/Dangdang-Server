package com.dangdang.server.domain.memberTown.domain.entity;

import com.dangdang.server.domain.memberTown.exception.NotAppropriateRangeException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.Arrays;

public enum RangeType {
  LEVEL1(1,1),
  LEVEL2(2,2),
  LEVEL3(3,4),
  LEVEL4(4,6);

  private final int rangeLevel;
  private final int distance;

  RangeType(int rangeLevel, int distance) {
    this.rangeLevel = rangeLevel;
    this.distance = distance;
  }

  public static RangeType getRangeType(int rangeLevel) {
    return Arrays.stream(RangeType.values())
        .filter(rangeType -> rangeType.rangeLevel == rangeLevel)
        .findFirst()
        .orElseThrow(() -> new NotAppropriateRangeException(ExceptionCode.NOT_EXIST_LEVEL));
  }
}
