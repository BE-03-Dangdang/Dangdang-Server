package com.dangdang.server.domain.pay.kftc.openBankingFacade.dto;

import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.domain.BankType;
import java.time.LocalDateTime;

public record OpenBankingInquiryReceiveResponse(Long payMemberId, String bankCode, String bankName,
                                                String receiveClientName,
                                                String accountNumber,
                                                LocalDateTime createdAt) {

  public static OpenBankingInquiryReceiveResponse of(Long payMemberId,
      BankOpenBankingApiResponse bankOpenBankingApiResponse, LocalDateTime createdAt) {
    BankType bankType = BankType.from(bankOpenBankingApiResponse.bankName());

    return new OpenBankingInquiryReceiveResponse(payMemberId, bankType.getBankCode(),
        bankType.getBankName(),
        bankOpenBankingApiResponse.clientName(), bankOpenBankingApiResponse.accountNumber(),
        createdAt);
  }
}
