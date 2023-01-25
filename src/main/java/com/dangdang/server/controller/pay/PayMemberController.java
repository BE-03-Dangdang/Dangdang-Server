package com.dangdang.server.controller.pay;

import static com.dangdang.server.global.exception.ExceptionCode.CHARGE_LESS_THAN_MIN_AMOUNT;
import static com.dangdang.server.global.exception.ExceptionCode.WITHDRAW_LESS_THAN_MIN_AMOUNT;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayType;
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
      @RequestBody @Valid PayRequest payRequest) {
    if (!PayType.CHARGE.checkMinAmount(payRequest.amount())) {
      throw new MinAmountException(CHARGE_LESS_THAN_MIN_AMOUNT);
    }

    Long memberId = ((Member) authentication.getPrincipal()).getId();
    PayResponse payResponse = payMemberService.charge(PayType.CHARGE, memberId, payRequest);

    return ResponseEntity.ok(payResponse);
  }

  /**
   * 당근머니 출금 API
   */
  @PatchMapping("/money/withdraw")
  public ResponseEntity<PayResponse> withdraw(Authentication authentication,
      @RequestBody @Valid PayRequest payRequest, BindingResult bindingResult) {
    if (!PayType.WITHDRAW.checkMinAmount(payRequest.amount())) {
      throw new MinAmountException(WITHDRAW_LESS_THAN_MIN_AMOUNT);
    }

    Long memberId = ((Member) authentication.getPrincipal()).getId();
    PayResponse payResponse = payMemberService.withdraw(PayType.WITHDRAW, memberId, payRequest);

    return ResponseEntity.ok(payResponse);
  }
}
