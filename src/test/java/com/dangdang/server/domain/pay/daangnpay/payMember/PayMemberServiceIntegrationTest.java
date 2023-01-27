package com.dangdang.server.domain.pay.daangnpay.payMember;

import static org.assertj.core.api.Assertions.assertThat;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.ConnectionAccountRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("당근페이 멤버 Service 통합테스트")
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
    member = new Member("01011111121", "예지 테스트 유저");
    memberRepository.save(member);

    String password = "password123";
    payMember = new PayMember(password, payMemberMoney, member);
    payMemberRepository.save(payMember);

    BankAccount bankAccount1 = new BankAccount("12383461723", "신한은행", 500000, payMember);
    BankAccount bankAccount2 = new BankAccount("34511234235", "우리은행", 40000, payMember);
    BankAccount bankAccount3 = new BankAccount("01290947732", "케이뱅크", 248200, payMember);

    bankAccounts = List.of(bankAccount1, bankAccount2, bankAccount3);
    bankAccountRepository.saveAll(bankAccounts);
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
        Long bankAccountId = bankAccount.getId();
        PayRequest payRequest = new PayRequest(bankAccountId, amountRequest);

        payResponse = payMemberService.withdraw(member.getId(),
            payRequest);

        assertThat(payResponse.bank()).isEqualTo(bankAccount.getBankName());
        assertThat(payResponse.accountNumber()).isEqualTo(bankAccount.getAccountNumber());
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
      Long bankAccountId = bankAccount.getId();
      PayRequest payRequest = new PayRequest(bankAccountId, amountRequest);

      PayResponse payResponse = payMemberService.charge(member.getId(), payRequest);

      assertThat(payResponse.money()).isEqualTo(payMemberMoney + amountRequest);
      assertThat(payResponse.bank()).isEqualTo(bankAccount.getBankName());
      assertThat(payResponse.accountNumber()).isEqualTo(bankAccount.getAccountNumber());
    }
  }
}