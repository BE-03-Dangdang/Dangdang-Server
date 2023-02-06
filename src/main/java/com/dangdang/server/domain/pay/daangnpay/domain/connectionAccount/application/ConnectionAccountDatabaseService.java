package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application;

import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_INACTIVE;
import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.PAY_MEMBER_NOT_FOUND;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.InactiveBankAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.ConnectionAccountRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.Position;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AllConnectionAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetConnectionAccountReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConnectionAccountDatabaseService {

  private final ConnectionAccountRepository connectionAccountRepository;
  private final PayMemberRepository payMemberRepository;
  private final BankAccountRepository bankAccountRepository;

  public ConnectionAccountDatabaseService(ConnectionAccountRepository connectionAccountRepository,
      PayMemberRepository payMemberRepository, BankAccountRepository bankAccountRepository) {
    this.connectionAccountRepository = connectionAccountRepository;
    this.payMemberRepository = payMemberRepository;
    this.bankAccountRepository = bankAccountRepository;
  }

  /**
   * 연결 계좌 추가
   */
  @Transactional
  public ConnectionAccount addConnectionAccount(Long memberId,
      AddConnectionAccountRequest addConnectionAccountRequest) {
    PayMember payMember = getPayMemberByMemberId(memberId);

    BankAccount bankAccount = bankAccountRepository.findById(
            addConnectionAccountRequest.bankAccountId())
        .orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));

    if (bankAccount.getStatus() == StatusType.INACTIVE) {
      throw new InactiveBankAccountException(BANK_ACCOUNT_INACTIVE);
    }

    ConnectionAccount newConnectionAccount;
    if (connectionAccountRepository.findByPayMemberId(
        payMember.getId()).size() == 0) {
      newConnectionAccount = new ConnectionAccount(bankAccount, payMember,
          Position.MAIN);
    } else {
      newConnectionAccount = new ConnectionAccount(bankAccount, payMember, Position.ADDITIONAL);
    }
    return connectionAccountRepository.save(newConnectionAccount);
  }

  /**
   * 내 연결 계좌 리스트 제공
   */
  public List<AllConnectionAccount> getAllConnectionAccount(Long memberId) {
    PayMember payMember = getPayMemberByMemberId(memberId);

    List<ConnectionAccount> connectionAccountList = connectionAccountRepository.findByPayMemberId(
        payMember.getId());

    return connectionAccountList.stream()
        .map(AllConnectionAccount::from)
        .toList();
  }

  public GetConnectionAccountReceiveResponse findIsMyAccountAndChargeAccount(
      Long payMemberId, String accountNumber) {
    List<ConnectionAccount> byPayMemberId = connectionAccountRepository.findByPayMemberId(
        payMemberId);

    boolean isMyAccount = byPayMemberId.stream()
        .anyMatch(connectionAccount -> connectionAccount.getBankAccountNumber()
            .equals(accountNumber));

    ConnectionAccount findChargeAccount = byPayMemberId.stream()
        .filter(connectionAccount -> connectionAccount.getPosition().equals(Position.MAIN))
        .findFirst().orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));

    return new GetConnectionAccountReceiveResponse(isMyAccount, findChargeAccount.getBank(),
        findChargeAccount.getBankAccountNumber());
  }

  public ConnectionAccount findByAccountNumber(String accountNumber) {
    return connectionAccountRepository.findByBankAccountNumber(
        accountNumber).orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));
  }

  public ConnectionAccount findMainConnectionAccountByPayMember(PayMember payMember) {
    List<ConnectionAccount> findConnectionAccount = connectionAccountRepository.findByPayMemberId(
        payMember.getId());
    return findConnectionAccount.stream()
        .filter(connectionAccount -> connectionAccount.getPosition().equals(Position.MAIN))
        .findFirst().orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));
  }

  private PayMember getPayMemberByMemberId(Long memberId) {
    return payMemberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new EmptyResultException(PAY_MEMBER_NOT_FOUND));
  }
}
