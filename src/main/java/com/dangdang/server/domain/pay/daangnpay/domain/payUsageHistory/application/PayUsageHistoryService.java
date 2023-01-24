package com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.application;

import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayType;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.domain.PayUsageHistoryRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.domain.entity.PayUsageHistory;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayUsageHistoryService {

  private static final int USAGE_HISTORY_ACCOUNT_LENGTH = 4;

  private final PayUsageHistoryRepository payUsageHistoryRepository;

  public PayUsageHistoryService(PayUsageHistoryRepository payUsageHistoryRepository) {
    this.payUsageHistoryRepository = payUsageHistoryRepository;
  }

  /**
   * 이용내역 추가
   */
  @Transactional
  public void addUsageHistory(PayType payType, OpenBankingResponse openBankingResponse, int money,
      PayMember payMember) {
    String bankName = openBankingResponse.bankName();
    String accountNumber = openBankingResponse.accountNumber();

    String usageHistoryTitle = bankName + " " + accountNumber;

    PayUsageHistory payUsageHistory = new PayUsageHistory(usageHistoryTitle, money, payMember,
        payType);
    payUsageHistoryRepository.save(payUsageHistory);
  }

  /**
   * TODO: 최근 이용내역 조회
   */
//  public void getUsageHistory() {
  //    accountNumber = accountNumber.substring(accountNumber.length() - USAGE_HISTORY_ACCOUNT_LENGTH);
//  }

}
