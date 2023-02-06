package com.dangdang.server.domain.pay.daangnpay.domain.payMember.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeeScheduler {

  private final PayMemberService payMemberService;

  public FeeScheduler(PayMemberService payMemberService) {
    this.payMemberService = payMemberService;
  }

  @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Seoul")
  private void feeScheduler() {
    payMemberService.initFiveFreeMonthlyFeeCount();
  }
}
