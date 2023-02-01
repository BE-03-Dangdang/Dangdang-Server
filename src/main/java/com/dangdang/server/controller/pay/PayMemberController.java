package com.dangdang.server.controller.pay;

import static com.dangdang.server.global.exception.ExceptionCode.BINDING_WRONG;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PostPayMemberSignupResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.PasswordSizeException;
import com.dangdang.server.global.aop.CurrentUserId;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
   * 당근페이 가입 API
   */
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("")
  public PostPayMemberSignupResponse createPayMember(@RequestParam String password,
      Authentication authentication) {
    if (password.length() < 6) {
      throw new PasswordSizeException(BINDING_WRONG);
    }
    Member member = (Member) authentication.getPrincipal();
    return payMemberService.signup(password, member);
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
