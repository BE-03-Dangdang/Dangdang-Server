package com.dangdang.server.domain.pay.daangnpay.payMember;

import static org.assertj.core.api.Assertions.assertThat;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.ConnectionAccountRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.domain.BankType;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@SpringBootTest
@DisplayName("당근페이 멤버 Service 통합테스트")
@ActiveProfiles("internal")
class PayMemberServiceIntegrationTest {

  static Member member;
  static List<BankAccount> bankAccounts;
  static PayMember payMember;
  final int payMemberMoney = 100000;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  PayMemberRepository payMemberRepository;
  @Autowired
  BankAccountRepository bankAccountRepository;
  @Autowired
  ConnectionAccountRepository connectionAccountRepository;
  @Autowired
  PayMemberService payMemberService;

  @BeforeEach
  void createPayMemberAndBankAccounts() {
    member = new Member("010", "예지 테스트 유저");
    memberRepository.save(member);

    String password = "password123";
    payMember = new PayMember(password, payMemberMoney, member.getId());
    payMemberRepository.save(payMember);

    BankAccount bankAccount1 = new BankAccount("12383461723", "신한은행", 500000, payMember, "홍길동");
    BankAccount bankAccount2 = new BankAccount("34511234235", "우리은행", 40000, payMember, "홍길동");
    BankAccount bankAccount3 = new BankAccount("01290947732", "케이뱅크", 248200, payMember, "홍길동");
    bankAccounts = List.of(bankAccount1, bankAccount2, bankAccount3);
    bankAccountRepository.saveAll(bankAccounts);

    ConnectionAccount connectionAccount = new ConnectionAccount(bankAccount1, payMember);
    connectionAccountRepository.save(connectionAccount);
  }

  @Nested
  @DisplayName("당근머니를 출금하면")
  class WithdrawTest {

    @Test
    @DisplayName("출금 금액만큼 줄어든 당근머니 잔액, 고객이 요청한 입금계좌 정보와 동일한 값이 리턴되어야 한다.")
    void withdraw() {
      final int amountRequest = 1000;
      int bankAccountSize = bankAccounts.size();
      PayResponse payResponse = null;

      for (BankAccount bankAccount : bankAccounts) {
        String accountNumber = bankAccount.getAccountNumber();
        PayRequest payRequest = new PayRequest(null, accountNumber, 1000);

        payResponse = payMemberService.withdraw(member.getId(),
            payRequest);

        assertThat(payResponse.bank()).isEqualTo(bankAccount.getBankName());
        assertThat(payResponse.accountNumber()).isEqualTo(accountNumber);
      }

      assert payResponse != null;
      assertThat(payResponse.money()).isEqualTo(payMemberMoney - (amountRequest * bankAccountSize));
    }
  }

  @Nested
  @DisplayName("당근머니를 충전하면")
  class ChargeTest {

    @Test
    @DisplayName("충전 완료된 당근머니 잔액, 고객이 요청한 출금계좌 정보와 동일한 값이 리턴되어야 한다.")
    void charge() {
      final int amountRequest = 10000;
      BankAccount bankAccount = bankAccounts.get(0);
      String accountNumber = bankAccount.getAccountNumber();
      PayRequest payRequest = new PayRequest(null, accountNumber, amountRequest);

      PayResponse payResponse = payMemberService.charge(member.getId(), payRequest);

      assertThat(payResponse.money()).isEqualTo(payMemberMoney + amountRequest);
      assertThat(payResponse.bank()).isEqualTo(bankAccount.getBankName());
      assertThat(payResponse.accountNumber()).isEqualTo(bankAccount.getAccountNumber());
    }
  }

  @Nested
  @DisplayName("수취조회를 실행하면")
  class InquiryReceive {

    final int minChargeAmount = 1000;
    int depositAmountRequest = 10000;

    @Nested
    @DisplayName("수취 계좌 예금주명, 당근페이 연결계좌 여부, 충전 정보(금액, 계좌), 수수료 정보를 반환한다.")
    class OpenApiAndConnectionAccountAndAutoChargeAmountAndFeeInfoTest {

      @Test
      @DisplayName("당근페이 연결계좌일 경우 isMyAccount는 true, 월 무료 수수료 횟수가 남았을 경우 횟수가 차감된다.")
      void whenMyConnectionAccountAndFreeMonthlyFeeCountIsExist() {
        BankAccount myBankAccount = bankAccounts.get(0);
        String bankCode = BankType.from(myBankAccount.getBankName()).getBankCode();
        int freeMonthlyFeeCount = payMember.getFreeMonthlyFeeCount();

        ReceiveRequest receiveRequest = new ReceiveRequest(null, depositAmountRequest,
            myBankAccount.getAccountNumber(), bankCode);
        ReceiveResponse receiveResponse = payMemberService.inquiryReceive(member.getId(),
            receiveRequest);

        assertThat(receiveResponse.receiveClientName()).isEqualTo(myBankAccount.getClientName());
        assertThat(receiveResponse.isMyAccount()).isTrue();
        assertThat(receiveResponse.feeAmount()).isZero();
        assertThat(receiveResponse.freeMonthlyFeeCount()).isEqualTo(freeMonthlyFeeCount - 1);
      }

      private void updateFreeMonthlyFeeCountIsZero(PayMember payMember) {
        while (payMember.getFreeMonthlyFeeCount() > 0) {
          payMember.changeFeeCount();
        }
      }

      @Test
      @DisplayName("타인계좌일 경우 isMyAccount는 false, 무료 수수료 횟수가 0일 경우 500원이 반환된다.")
      void whenNotMyAccountAndFreeMonthlyFeeCountIsZero() {
        BankAccount otherBankAccount = bankAccounts.get(1);
        updateFreeMonthlyFeeCountIsZero(payMember);

        String bankCode = BankType.from(otherBankAccount.getBankName()).getBankCode();

        ReceiveRequest receiveRequest = new ReceiveRequest(null, depositAmountRequest,
            otherBankAccount.getAccountNumber(), bankCode);
        ReceiveResponse receiveResponse = payMemberService.inquiryReceive(member.getId(),
            receiveRequest);

        assertThat(receiveResponse.receiveClientName()).isEqualTo(otherBankAccount.getClientName());
        assertThat(receiveResponse.isMyAccount()).isFalse();
        assertThat(receiveResponse.feeAmount()).isEqualTo(500);
        assertThat(receiveResponse.freeMonthlyFeeCount()).isZero();
      }

      private int calculateAutoChargeAmount() {
        int difference = depositAmountRequest - payMember.getMoney();
        if (difference < 0) {
          difference = 0;
        }
        return difference;
      }

      @Nested
      @DisplayName("자동충전은 최소 1000원이며 연결계좌에서 실행된다.")
      class AutoChargeAmount {

        @ParameterizedTest
        @ValueSource(ints = {999, 1000, 2000})
        @DisplayName("입금 이체 요청 금액 > 당근머니 잔액 : Max(최소 충전 금액, 요청 금액과 당근머니의 차액)")
        void whenSmallRequestAmountThanMoneyBalance(int diff) {
          BankAccount bankAccount = bankAccounts.get(0);
          depositAmountRequest = payMember.getMoney() + diff;
          String bankCode = BankType.from(bankAccount.getBankName()).getBankCode();
          int difference = calculateAutoChargeAmount();

          ReceiveRequest receiveRequest = new ReceiveRequest(null, depositAmountRequest,
              bankAccount.getAccountNumber(), bankCode);
          ReceiveResponse receiveResponse = payMemberService.inquiryReceive(member.getId(),
              receiveRequest);

          assertThat(receiveResponse.chargeAccountBankName()).isEqualTo(bankAccount.getBankName());
          assertThat(receiveResponse.chargeAccountNumber()).isEqualTo(
              bankAccount.getAccountNumber());
          assertThat(receiveResponse.autoChargeAmount()).isEqualTo(
              Math.max(difference, minChargeAmount));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        @DisplayName("입금 이체 요청 금액 <= 당근머니 잔액 : 0원 충전")
        void whenLargeRequestAmountThanMoneyBalance(int diff) {
          BankAccount bankAccount = bankAccounts.get(0);
          depositAmountRequest = payMember.getMoney() + diff;
          String bankCode = BankType.from(bankAccount.getBankName()).getBankCode();

          ReceiveRequest receiveRequest = new ReceiveRequest(null, depositAmountRequest,
              bankAccount.getAccountNumber(), bankCode);
          ReceiveResponse receiveResponse = payMemberService.inquiryReceive(member.getId(),
              receiveRequest);

          assertThat(receiveResponse.chargeAccountBankName()).isEqualTo(bankAccount.getBankName());
          assertThat(receiveResponse.chargeAccountNumber()).isEqualTo(
              bankAccount.getAccountNumber());
          assertThat(receiveResponse.autoChargeAmount()).isZero();
        }

      }
    }
  }
}
