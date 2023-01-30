package com.dangdang.server.domain.memberTown.dto.request;

import java.math.BigDecimal;

public record MemberTownCertifyRequest(BigDecimal longitude, BigDecimal latitude) {
}
