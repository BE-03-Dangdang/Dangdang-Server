package com.dangdang.server.domain.pay.banks.bankAccount.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.BankAccountAuthenticationException;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.InactiveBankAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.InsufficientBankAccountException;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingWithdrawRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankAccountService 단위 테스트")
class BankAccountServiceTest {

  @Mock
  BankAccountRepository bankAccountRepository;
  @Spy
  @InjectMocks
  BankAccountService bankAccountService = new BankAccountService(bankAccountRepository);

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
        int balance = 1000;
        int result = 400;
        OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(1L,
            1L,
            1L, amountReq);
        BankAccount bankAccount = new BankAccount("11239847", "신한은행", balance,
            new PayMember("password", new Member("닉네임", "핸드폰")));

        doReturn(1L).when(bankAccountService).getPayMemberIdFromAccount(any());
        doReturn(bankAccount).when(bankAccountService).findById(any());

        bankAccountService.withdraw(openBankingWithdrawRequest);

        assertThat(bankAccount.getBalance()).isEqualTo(result);
      }
    }

    @Nested
    @DisplayName("실패")
    class whenFail {

      @Test
      @DisplayName("출금계좌에 잔액이 없다면 InsufficientBankAccountException이 발생한다.")
      void zeroBalance() {
        OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(1L,
            1L,
            1L, 10000);
        BankAccount bankAccount = new BankAccount("11239847", "신한은행", 0,
            new PayMember("password", new Member("닉네임", "핸드폰")));

        doReturn(1L).when(bankAccountService).getPayMemberIdFromAccount(any());
        doReturn(bankAccount).when(bankAccountService).findById(any());

        assertThrows(InsufficientBankAccountException.class,
            () -> bankAccountService.withdraw(openBankingWithdrawRequest));
      }

      @Test
      @DisplayName("출금계좌 상태가 inactive인 경우 InactiveBankAccountExceptionException이 발생한다.")
      void inactiveAccount() {
        OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(1L,
            1L,
            1L, 10000);
        BankAccount bankAccount = new BankAccount("11239847", "신한은행", 1000,
            new PayMember("password", new Member("닉네임", "핸드폰")), StatusType.INACTIVE);

        doReturn(bankAccount).when(bankAccountService).findById(any());

        assertThrows(InactiveBankAccountException.class,
            () -> bankAccountService.withdraw(openBankingWithdrawRequest));
      }

      @Test
      @DisplayName("출금계좌의 payMemberId와 요청값인 payMemberId가 일치하지 않는다면 BankAccountAuthenticationException이 발생한다.")
      void failAuth() {
        OpenBankingWithdrawRequest openBankingWithdrawRequest = new OpenBankingWithdrawRequest(2L,
            1L,
            1L, 10000);
        BankAccount bankAccount = new BankAccount("11239847", "신한은행", 1000,
            new PayMember("password", new Member("닉네임", "핸드폰")));

        doReturn(1L).when(bankAccountService).getPayMemberIdFromAccount(any());
        doReturn(bankAccount).when(bankAccountService).findById(any());

        assertThrows(BankAccountAuthenticationException.class,
            () -> bankAccountService.withdraw(openBankingWithdrawRequest));
      }
    }
  }

}