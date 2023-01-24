package com.dangdang.server.domain.post.dto.request;

import com.dangdang.server.domain.common.StatusType;
import javax.validation.constraints.NotNull;

public record PostUpdateStatusRequest(@NotNull StatusType status) {
}
