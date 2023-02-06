package com.dangdang.server.domain.pay.kftc.common.dto;

import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostDepositResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostWithdrawResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.domain.BankType;
import java.time.LocalDateTime;

public record OpenBankingResponse(Long payMemberId, String bankCode, String bankName,
                                  String accountNumber,
                                  LocalDateTime createdAt) {

  public static OpenBankingResponse ofInternal(Long payMemberId,
      BankOpenBankingApiResponse bankOpenBankingApiResponse, LocalDateTime createdAt) {
    BankType bankType = BankType.from(bankOpenBankingApiResponse.bankName());

    return new OpenBankingResponse(payMemberId, bankType.getBankCode(), bankType.getBankName(),
        bankOpenBankingApiResponse.accountNumber(), createdAt);
  }

  public static OpenBankingResponse ofExternal(Long payMemberId, String accountNumber,
      PostWithdrawResponse postWithdrawResponse, LocalDateTime createdAt) {
    return new OpenBankingResponse(payMemberId, postWithdrawResponse.bank_code_std(),
        postWithdrawResponse.bank_name(), accountNumber, createdAt);
  }

  public static OpenBankingResponse ofExternal(Long payMemberId, String accountNumber,
      PostDepositResponse postDepositResponse, LocalDateTime createdAt) {
    return new OpenBankingResponse(payMemberId,
        postDepositResponse.res_list().get(0).getBank_code_std(),
        postDepositResponse.res_list().get(0).getBank_name(), accountNumber, createdAt);
  }
}
