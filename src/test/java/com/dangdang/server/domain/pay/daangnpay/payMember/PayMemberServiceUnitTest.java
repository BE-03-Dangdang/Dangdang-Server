package com.dangdang.server.domain.pay.daangnpay.payMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetConnectionAccountReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.application.PayUsageHistoryService;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.application.OpenBankingFacadeService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PayMemberService 단위테스트")
public class PayMemberServiceUnitTest {

  @Spy
  @InjectMocks
  PayMemberService payMemberService;
  @Mock
  PayMemberRepository payMemberRepository;
  @Mock
  OpenBankingFacadeService openBankingFacadeService;
  @Mock
  PayUsageHistoryService payUsageHistoryService;
  @Mock
  ConnectionAccountDatabaseService connectionAccountDatabaseService;
  int moneyBalance = 50000;
  int freeFeeCount = 0;
  PayMember payMember = new PayMember(moneyBalance, freeFeeCount);

  @Nested
  @DisplayName("수취 조회를 실행하면")
  class InquiryReceive {

    int depositAmountRequest = 10000;

    ReceiveRequest receiveRequest = new ReceiveRequest(null, depositAmountRequest, "38471032472",
        "097");

    @Test
    @DisplayName("수취 계좌 예금주명과 당근페이 연결계좌 여부, 충전 계좌 정보를 확인하고")
    void checkOpenApiAndConnectionAccountTest() {
      checkOpenApiAndConnectionAccount();
      payMemberService.inquiryReceive(null, receiveRequest);

      verify(openBankingFacadeService, times(1)).inquiryReceive(any());
      verify(connectionAccountDatabaseService,
          times(1)).findIsMyAccountAndChargeAccount(any(), any());
    }

    void checkOpenApiAndConnectionAccount() {
      OpenBankingInquiryReceiveResponse openBankingInquiryReceiveResponse = new OpenBankingInquiryReceiveResponse(
          1L, "신한은행", "홍길동", "38471032472", LocalDateTime.now());
      GetConnectionAccountReceiveResponse getConnectionAccountReceiveResponse = new GetConnectionAccountReceiveResponse(
          true, "하나은행", "234719284");

      doReturn(Optional.of(payMember)).when(payMemberRepository).findByMemberId(any());
      doReturn(openBankingInquiryReceiveResponse).when(openBankingFacadeService)
          .inquiryReceive(any());
      doReturn(getConnectionAccountReceiveResponse).when(connectionAccountDatabaseService)
          .findIsMyAccountAndChargeAccount(any(), any());
    }

    @Nested
    @DisplayName("자동 충전 금액을 계산한다.")
    class CalculateAutoChargeTest {

      @Test
      @DisplayName("입금 이체 요청 금액 <= 당근머니 잔액 : 0원 충전")
      void whenLargeRequestAmountThanMoneyBalance() {
        moneyBalance = depositAmountRequest;
        payMember = new PayMember(moneyBalance, freeFeeCount);

        checkOpenApiAndConnectionAccount();
        ReceiveResponse receiveResponse = payMemberService.inquiryReceive(null, receiveRequest);

        assertThat(receiveResponse.autoChargeAmount()).isZero();
      }

      @Test
      @DisplayName("입금 이체 요청 금액 > 당근머니 잔액 : Max(최소 충전 금액, 요청 금액과 당근머니의 차액)")
      void whenSmallRequestAmountThanMoneyBalance() {
        final int minChargeAmount = 1000;
        int difference = 5000;
        moneyBalance = depositAmountRequest - difference;
        payMember = new PayMember(moneyBalance, freeFeeCount);
        int max = Math.max(minChargeAmount, difference);

        checkOpenApiAndConnectionAccount();
        ReceiveResponse receiveResponse = payMemberService.inquiryReceive(null, receiveRequest);

        assertThat(receiveResponse.autoChargeAmount()).isEqualTo(max);
      }
    }

    @Nested
    @DisplayName("수수료 정보를 확인한다.")
    class CheckFeeInfoTest {

      @Test
      @DisplayName("고객의 남은 월 무료 수수료 횟수가 존재한다면, 횟수가 1회 차감되고 수수료는 무료이다.")
      void whenExistFreeFeeCount() {
        freeFeeCount = 5;
        payMember = new PayMember(moneyBalance, freeFeeCount);

        checkOpenApiAndConnectionAccount();
        ReceiveResponse receiveResponse = payMemberService.inquiryReceive(null, receiveRequest);

        assertThat(receiveResponse.feeAmount()).isZero();
        assertThat(receiveResponse.freeMonthlyFeeCount()).isEqualTo(freeFeeCount - 1);
      }

      @Test
      @DisplayName("고객의 남은 월 무료 수수료 횟수가 없다면, 수수료는 500원이다.")
      void whenZeroFreeFeeCount() {
        freeFeeCount = 0;
        payMember = new PayMember(moneyBalance, freeFeeCount);

        checkOpenApiAndConnectionAccount();
        ReceiveResponse receiveResponse = payMemberService.inquiryReceive(null, receiveRequest);

        assertThat(receiveResponse.feeAmount()).isEqualTo(500);
        assertThat(receiveResponse.freeMonthlyFeeCount()).isZero();
      }
    }
  }

}
