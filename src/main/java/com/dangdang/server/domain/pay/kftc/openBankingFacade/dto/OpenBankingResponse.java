package com.dangdang.server.domain.pay.kftc.openBankingFacade.dto;

import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.domain.BankType;
import java.time.LocalDateTime;

public record OpenBankingResponse(Long payMemberId, String bankCode, String bankName,
                                  String accountNumber,
                                  LocalDateTime createdAt) {

  public static OpenBankingResponse of(Long payMemberId,
      BankOpenBankingApiResponse bankOpenBankingApiResponse, LocalDateTime createdAt) {
    BankType bankType = BankType.from(bankOpenBankingApiResponse.bankName());

    return new OpenBankingResponse(payMemberId, bankType.getBankCode(), bankType.getBankName(),
        bankOpenBankingApiResponse.accountNumber(), createdAt);
  }
}
