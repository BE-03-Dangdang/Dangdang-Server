package com.dangdang.server.domain.pay.banks.bankAccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.BankAccountAuthenticationException;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.InactiveBankAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.InsufficientBankAccountException;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("BankAccountService 통합 테스트")
@ActiveProfiles("internal")
class BankAccountServiceIntegrationTest {

  @Autowired
  BankAccountRepository bankAccountRepository;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  PayMemberRepository payMemberRepository;
  @Autowired
  BankAccountService bankAccountService;

  PayMember payMember;
  BankAccount bankAccount;
  BankAccount bankAccountInactive;
  int beforeBankBalance = 1000;
  String fintechUseNum = "128947";

  @BeforeEach
  void setUp() {
    Member member = new Member("닉네임", "핸드폰");
    memberRepository.save(member);

    payMember = new PayMember("password", member);
    payMemberRepository.save(payMember);

    bankAccountInactive = new BankAccount("11239847", "신한은행", 1000, payMember, "홍길동",
        StatusType.INACTIVE);
    bankAccountRepository.save(bankAccountInactive);

    bankAccount = new BankAccount("1123967847", "신한은행", 1000, payMember, "홍길동");
    bankAccountRepository.save(bankAccount);
  }

  @Nested
  @DisplayName("은행 계좌에 출금 요청이 들어왔을 때")
  class withdrawTest {

    @Nested
    @DisplayName("성공")
    class whenSuccess {

      @Test
      @DisplayName("정상계좌이면 요청 금액만큼 요청 계좌에서 돈이 출금된다.")
      void successWithdraw() {
        int amountReq = 600;
        int balance = bankAccount.getBalance();
        int result = balance - amountReq;

        OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(
            payMember.getId(), null, fintechUseNum, "1111", bankAccount.getAccountNumber(),
            amountReq);

        bankAccountService.withdraw(openBankingWithdrawRequest);

        assertThat(bankAccount.getBalance()).isEqualTo(result);
      }
    }

    @Nested
    @DisplayName("실패")
    class whenFail {

      @Nested
      @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
      @TestInstance(Lifecycle.PER_CLASS)
      @DisplayName("출금계좌 상태가 inactive인 경우")
      class InactiveAccount {

        @Order(1)
        @Test
        @DisplayName("InactiveBankAccountException이 발생하고")
        void inactiveBankAccountException() {
          OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(
              payMember.getId(), null, fintechUseNum, "1111",
              bankAccountInactive.getAccountNumber(), 10000);

          assertThrows(InactiveBankAccountException.class,
              () -> bankAccountService.withdraw(openBankingWithdrawRequest));
        }

        @Order(2)
        @Test
        @DisplayName("계좌 잔액에 변화가 없어야 한다.")
        void notChangeBalance() {
          assertThat(bankAccountInactive.getBalance()).isEqualTo(beforeBankBalance);
        }
      }

      @Nested
      @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
      @TestInstance(Lifecycle.PER_CLASS)
      @DisplayName("출금계좌의 payMemberId와 요청값인 payMemberId가 일치하지 않는다면 ")
      class FailAuth {

        @Order(1)
        @Test
        @DisplayName("BankAccountAuthenticationException이 발생한다.")
        void bankAccountAuthenticationException() {
          Long payMemberIdReq = Long.MAX_VALUE;
          OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(
              payMemberIdReq, null, fintechUseNum, "11231", bankAccount.getAccountNumber(), 10000);

          assertThrows(BankAccountAuthenticationException.class,
              () -> bankAccountService.withdraw(openBankingWithdrawRequest));
        }

        @Order(2)
        @Test
        @DisplayName("계좌 잔액에 변화가 없어야 한다.")
        void notChangeBalance() {
          assertThat(bankAccount.getBalance()).isEqualTo(beforeBankBalance);
        }
      }

      @Nested
      @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
      @TestInstance(Lifecycle.PER_CLASS)
      @DisplayName("출금계좌 잔액보다 큰 금액을 요청했을 경우")
      class BankAccountZeroBalance {

        @Order(1)
        @Test
        @DisplayName("InsufficientBankAccountException이 발생하고")
        void insufficientBankAccountException() {
          int amountReq = beforeBankBalance + 1000;
          OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(
              payMember.getId(), null, fintechUseNum, "1111", bankAccount.getAccountNumber(),
              amountReq);

          assertThrows(InsufficientBankAccountException.class,
              () -> bankAccountService.withdraw(openBankingWithdrawRequest));
        }

        @Order(2)
        @Test
        @DisplayName("계좌 잔액에 변화가 없어야 한다.")
        void notChangeBalance() {
          assertThat(bankAccount.getBalance()).isEqualTo(beforeBankBalance);
        }

      }
    }
  }
}