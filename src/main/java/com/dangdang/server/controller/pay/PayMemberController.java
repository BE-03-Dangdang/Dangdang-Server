package com.dangdang.server.controller.pay;

import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
import com.dangdang.server.global.aop.CurrentUserId;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay-members")
public class PayMemberController {


  private final PayMemberService payMemberService;

  public PayMemberController(PayMemberService payMemberService) {
    this.payMemberService = payMemberService;
  }

  /**
   * 당근머니 충전 API
   */
  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/money/charge")
  public PayResponse charge(Long memberId, @RequestBody @Valid PayRequest payRequest) {
    return payMemberService.charge(memberId, payRequest);
  }

  /**
   * 당근머니 출금 API
   */
  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/money/withdraw")
  public PayResponse withdraw(Long memberId, @RequestBody @Valid PayRequest payRequest) {
    return payMemberService.withdraw(memberId, payRequest);
  }

  /**
   * 수취 조회 API
   */
  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/inquiry/receive")
  public ReceiveResponse inquiryReceive(Long memberId,
      @Valid @RequestBody ReceiveRequest receiveRequest) {
    return payMemberService.inquiryReceive(memberId, receiveRequest);
  }
}
