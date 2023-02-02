package com.dangdang.server.controller.pay;

import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PostPayMemberRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetUserMeResponse;
import com.dangdang.server.global.aop.CurrentUserId;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 외부 연동 controller
 */
@RestController
@RequestMapping("/open-banking")
public class PayExternalController {

  private final PayMemberService payMemberService;

  public PayExternalController(PayMemberService payMemberService) {
    this.payMemberService = payMemberService;
  }

  /**
   * 오픈뱅킹 사용자 생성 Member, PayMember 가입 선처리 필수 // paySignup.html 에서 호출됨
   */
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("")
  public void createOpenBankingMemberFromState(
      @RequestBody @Valid PostPayMemberRequest postPayMemberRequest) {
    payMemberService.createOpenBankingMemberFromState(postPayMemberRequest);
  }

  /**
   * 오픈API 토큰 발급 (최초 1번만 유효함, 재요청 시 거부) Auth 인증 및 계좌 등록 후 callback // paySignup.html 에서 인증 완료 후 자동
   * 호출됨
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/auth-result")
  public GetAuthTokenResponse createToken(@RequestParam String code, @RequestParam String scope,
      @RequestParam String client_info, @RequestParam String state) {
    GetAuthTokenRequest getAuthTokenRequest = new GetAuthTokenRequest(code, state);
    return payMemberService.getAuthTokenResponse(getAuthTokenRequest);
  }

  /**
   * 사용자 정보 조회 테스트베드 데이터 (token, userSeqNo) : properties 참고
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/me")
  public GetUserMeResponse getOpenApiTestUserInfo(
      @RequestHeader(value = "Authorization") String token, @RequestParam String userSeqNo) {
    return payMemberService.getUserMeResponse(token, userSeqNo);
  }

  /**
   * 당근머니 충전
   */
  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/transfer/withdraw/fin_num")
  public PayResponse charge(Long memberId, @RequestBody PayRequest payRequest) {
    return payMemberService.charge(memberId, payRequest);
  }


}
