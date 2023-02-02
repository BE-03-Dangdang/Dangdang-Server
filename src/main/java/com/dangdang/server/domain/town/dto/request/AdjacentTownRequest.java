package com.dangdang.server.domain.town.dto.request;

import com.dangdang.server.domain.memberTown.domain.entity.RangeType;

public record AdjacentTownRequest(long townId, RangeType rangeType) {
}
