package com.dangdang.server.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

public record MemberRefreshRequest(
    @NotBlank
    String refreshToken
) {

}
