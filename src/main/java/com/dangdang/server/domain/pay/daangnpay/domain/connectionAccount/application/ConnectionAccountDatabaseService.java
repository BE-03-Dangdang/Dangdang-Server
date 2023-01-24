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
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
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
    BankAccount bankAccount = bankAccountRepository.findById(
            addConnectionAccountRequest.bankAccountId())
        .orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));

    if (bankAccount.getStatus() == StatusType.INACTIVE) {
      throw new InactiveBankAccountException(BANK_ACCOUNT_INACTIVE);
    }

    PayMember payMember = payMemberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new EmptyResultException(PAY_MEMBER_NOT_FOUND));

    ConnectionAccount connectionAccount = new ConnectionAccount(bankAccount, payMember);
    return connectionAccountRepository.save(connectionAccount);
  }
}
