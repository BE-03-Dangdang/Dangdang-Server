package com.dangdang.server.domain.pay.kftc.common.dto;

import javax.validation.constraints.Min;

public record OpenBankingDepositRequest(Long payMemberId, String openBankingToken,
                                        String fintechUseNum, String payMemberName,
                                        String payMemberConnectionAccountBank,
                                        String payMemberConnectionAccountNumber,
                                        String toBankName, String toBankAccountNumber,
                                        String fromTrustAccountNumber, @Min(1) Integer amount) {

}
