package com.dangdang.server.controller.pay;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @PatchMapping("/money/charge")
  public ResponseEntity<PayResponse> charge(Authentication authentication,
      @RequestBody @Valid PayRequest payRequest) {
    Long memberId = ((Member) authentication.getPrincipal()).getId();
    PayResponse payResponse = payMemberService.charge(memberId, payRequest);

    return ResponseEntity.ok(payResponse);
  }

  /**
   * 당근머니 출금 API
   */
  @PatchMapping("/money/withdraw")
  public ResponseEntity<PayResponse> withdraw(Authentication authentication,
      @RequestBody @Valid PayRequest payRequest) {
    Long memberId = ((Member) authentication.getPrincipal()).getId();
    PayResponse payResponse = payMemberService.withdraw(memberId, payRequest);

    return ResponseEntity.ok(payResponse);
  }
}
