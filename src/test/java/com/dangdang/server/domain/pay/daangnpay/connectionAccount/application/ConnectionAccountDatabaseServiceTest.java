package com.dangdang.server.domain.pay.daangnpay.connectionAccount.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.InactiveBankAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.ConnectionAccountRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("연결계좌 DB 테스트")
class ConnectionAccountDatabaseServiceTest {

  static Member member;
  static PayMember payMember;
  static List<BankAccount> bankAccounts;

  @Autowired
  MemberRepository memberRepository;
  @Autowired
  PayMemberRepository payMemberRepository;
  @Autowired
  BankAccountRepository bankAccountRepository;
  @Autowired
  ConnectionAccountRepository connectionAccountRepository;
  @Autowired
  ConnectionAccountDatabaseService connectionAccountDataBaseService;

  @BeforeEach
  void createPayMemberAndBankAccounts() {
    member = new Member("예지 테스트 유저", "01012341234");
    memberRepository.save(member);

    String password = "password123";
    payMember = new PayMember(password, member);
    payMemberRepository.save(payMember);

    BankAccount bankAccount1 = new BankAccount("12383461723", "신한은행", 500000, payMember);
    BankAccount bankAccount2 = new BankAccount("34511234235", "우리은행", 40000, payMember);
    BankAccount bankAccount3 = new BankAccount("01290947732", "케이뱅크", 248200, payMember);

    bankAccounts = List.of(bankAccount1, bankAccount2, bankAccount3);
    bankAccountRepository.saveAll(bankAccounts);
  }

  @TestInstance(Lifecycle.PER_CLASS)
  @Nested
  @DisplayName("연결계좌를 새로 추가할 때")
  class addConnectionAccount {

    @Test
    @DisplayName("성공한다면 bankAccount 정보와 payMemberId가 저장된다.")
    void successAdd() {
      for (BankAccount bankAccount : bankAccounts) {
        AddConnectionAccountRequest addConnectionAccountRequest = new AddConnectionAccountRequest(
            bankAccount.getId());

        ConnectionAccount connectionAccount = connectionAccountDataBaseService.addConnectionAccount(
            payMember.getId(), addConnectionAccountRequest);

        assertThat(connectionAccount.getBankAccountNumber()).isEqualTo(
            bankAccount.getAccountNumber());
        assertThat(connectionAccount.getBank()).isEqualTo(bankAccount.getBankName());
        assertThat(connectionAccount.getPayMember().getId()).isEqualTo(payMember.getId());
      }
    }

    @Test
    @DisplayName("사용 불가능 계좌일 경우 추가할 수 없다.")
    void failAdd() {
      BankAccount bankAccount = new BankAccount("12383461723", "신한은행", 500000, payMember,
          StatusType.INACTIVE);
      bankAccountRepository.save(bankAccount);

      AddConnectionAccountRequest addConnectionAccountRequest = new AddConnectionAccountRequest(
          bankAccount.getId());
      Long payMemberId = payMember.getId();

      assertThrows(InactiveBankAccountException.class,
          () -> connectionAccountDataBaseService.addConnectionAccount(payMemberId,
              addConnectionAccountRequest));
    }
  }
}