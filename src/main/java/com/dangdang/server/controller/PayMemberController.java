package com.dangdang.server.controller;

import static com.dangdang.server.global.exception.ExceptionCode.LESS_THAN_MIN_AMOUNT;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.MinAmountException;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
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
      @RequestBody @Valid PayRequest payRequest, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new MinAmountException(LESS_THAN_MIN_AMOUNT);
    }

    Long memberId = ((Member) authentication.getPrincipal()).getId();
    PayResponse payResponse = payMemberService.charge(memberId, payRequest);

    return ResponseEntity.ok(payResponse);
  }

}
