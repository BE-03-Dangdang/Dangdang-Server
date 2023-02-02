package com.dangdang.server.domain.pay.daangnpay.payUsageHistory;

import static org.assertj.core.api.Assertions.assertThat;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayType;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.application.PayUsageHistoryService;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.domain.PayUsageHistoryRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.domain.entity.PayUsageHistory;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.domain.BankType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class PayUsageHistoryServiceIntegrationTest {

  static Member member;
  static PayMember payMember;
  @Autowired
  PayUsageHistoryService payUsageHistoryService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  PayMemberRepository payMemberRepository;
  @Autowired
  PayUsageHistoryRepository payUsageHistoryRepository;


  @BeforeEach
  void createPayMemberAndBankAccounts() {
    member = new Member("예지 테스트 유저", "01012341234");
    memberRepository.save(member);

    String password = "password123";
    payMember = new PayMember(password, member);
    payMemberRepository.save(payMember);
  }

  @Test
  @DisplayName("이용내역 추가 성공 테스트")
  void addUsageHistory() {
    OpenBankingResponse openBankingResponse = new OpenBankingResponse(payMember.getId(),
        BankType.SC.getBankCode(),
        "수협은행", "1283362446",
        LocalDateTime.now());

    String usageHistoryTitle = "수협은행 1283362446";

    payUsageHistoryService.addUsageHistory(PayType.WITHDRAW, openBankingResponse, 1000, payMember);
    PayUsageHistory payUsageHistory = payUsageHistoryRepository.findByTitle(usageHistoryTitle)
        .orElseThrow();

    assertThat(payUsageHistory.getPayType()).isEqualTo(PayType.WITHDRAW);
  }
}