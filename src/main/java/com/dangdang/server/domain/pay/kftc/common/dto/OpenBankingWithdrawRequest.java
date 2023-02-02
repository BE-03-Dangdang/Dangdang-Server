package com.dangdang.server.domain.pay.kftc.common.dto;

import javax.validation.constraints.Min;

public record OpenBankingWithdrawRequest(Long payMemberId, String openBankingToken,
                                         String fintechUseNum, String toTrustAccountNumber,
                                         String accountHolder, String fromBankAccountNumber,
                                         @Min(1) Integer amount) {

}
