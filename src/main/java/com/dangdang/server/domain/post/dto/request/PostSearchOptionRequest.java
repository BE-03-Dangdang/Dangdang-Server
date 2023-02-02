package com.dangdang.server.domain.post.dto.request;

import com.dangdang.server.domain.post.domain.Category;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record PostSearchOptionRequest(
    List<Category> category,
    @Min(0) Long minPrice,
    @Max(9_999_999_999L) Long maxPrice,
    @NotNull @Min(1) @Max(4) int rangeLevel,
    Boolean isTransactionAvailableOnly
) {
}
