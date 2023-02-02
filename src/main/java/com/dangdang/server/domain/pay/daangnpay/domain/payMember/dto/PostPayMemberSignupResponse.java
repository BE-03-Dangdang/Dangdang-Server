package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

public record PostPayMemberSignupResponse(
    Long payMemberId) {

  public static PostPayMemberSignupResponse from(Long payMemberId) {
    return new PostPayMemberSignupResponse(payMemberId);
  }
}
