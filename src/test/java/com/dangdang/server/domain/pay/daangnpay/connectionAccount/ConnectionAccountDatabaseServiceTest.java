package com.dangdang.server.domain.pay.daangnpay.connectionAccount;

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
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetAllConnectionAccountResponse;
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

    BankAccount bankAccount1 = new BankAccount("12383461723", "신한은행", 500000, payMember, "홍길동");
    BankAccount bankAccount2 = new BankAccount("34511234235", "우리은행", 40000, payMember, "홍길동");
    BankAccount bankAccount3 = new BankAccount("01290947732", "케이뱅크", 248200, payMember, "홍길동");

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
            member.getId(), addConnectionAccountRequest);

        assertThat(connectionAccount.getBankAccountNumber()).isEqualTo(
            bankAccount.getAccountNumber());
        assertThat(connectionAccount.getBank()).isEqualTo(bankAccount.getBankName());
        assertThat(connectionAccount.getPayMember().getId()).isEqualTo(payMember.getId());
      }
    }

    @Test
    @DisplayName("사용 불가능 계좌일 경우 추가할 수 없다.")
    void failAdd() {
      BankAccount bankAccount = new BankAccount("12383461723", "신한은행", 500000, payMember, "홍길동",
          StatusType.INACTIVE);
      bankAccountRepository.save(bankAccount);

      AddConnectionAccountRequest addConnectionAccountRequest = new AddConnectionAccountRequest(
          bankAccount.getId());
      Long memberId = member.getId();

      assertThrows(InactiveBankAccountException.class,
          () -> connectionAccountDataBaseService.addConnectionAccount(memberId,
              addConnectionAccountRequest));
    }
  }

  @Nested
  @DisplayName("연결계좌 리스트를 조회했을 때")
  class getAllConnectionAccountTest {

    int allBankAccountSize = 0;
    List<BankAccount> allBankAccount;

    @BeforeEach
    void setUp() {
      allBankAccount = bankAccountRepository.findAll();
      allBankAccountSize = allBankAccount.size();
    }

    @Nested
    @DisplayName("성공")
    class Success {

      @Nested
      @DisplayName("고객의 당근페이 연결계좌가 존재할 경우")
      class WhenExistConnectionAccount {

        @Test
        @DisplayName("전체 리스트가 조회된다.")
        void getAllConnectionAccountList() {
          for (BankAccount account : allBankAccount) {
            ConnectionAccount connectionAccount = new ConnectionAccount(account, payMember);
            connectionAccountRepository.save(connectionAccount);
          }

          Long memberId = member.getId();

          List<GetAllConnectionAccountResponse> allConnectionAccount = connectionAccountDataBaseService.getAllConnectionAccount(
              memberId);

          assertThat(allConnectionAccount).hasSize(allBankAccountSize);
        }
      }

      @Nested
      @DisplayName("고객의 당근페이 연결계좌가 존재하지 않을 경우")
      class WhenZeroConnectionAccount {

        @Test
        @DisplayName("빈 리스트가 조회된다.")
        void getZeroList() {
          Long memberId = member.getId();

          List<GetAllConnectionAccountResponse> allConnectionAccount = connectionAccountDataBaseService.getAllConnectionAccount(
              memberId);

          assertThat(allConnectionAccount).isEmpty();
        }
      }
    }
  }

}