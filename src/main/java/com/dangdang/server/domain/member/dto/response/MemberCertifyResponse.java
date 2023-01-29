package com.dangdang.server.domain.member.dto.response;

public record MemberCertifyResponse (
    String accessToken,
    Boolean isCertified
) {

}
