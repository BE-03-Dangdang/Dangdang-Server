package com.dangdang.server.domain.post.dto.request;

import static com.dangdang.server.global.exception.ExceptionCode.*;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.common.exception.InvalidStatusTypeException;
import com.dangdang.server.global.exception.ExceptionCode;
import javax.validation.constraints.NotNull;

public record PostUpdateStatusRequest(@NotNull StatusType status) {

  public PostUpdateStatusRequest {
    verifyValidStatus(status);
  }

  private void verifyValidStatus(StatusType status) {
    switch(status) {
      case SELLING, RESERVED, COMPLETED -> {}
      default -> throw new InvalidStatusTypeException(INVALID_POST_STATUS);
    }
  }
}
