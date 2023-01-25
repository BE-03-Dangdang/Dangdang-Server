package com.dangdang.server.domain.pay.kftc.openBankingFacade.dto;

import javax.validation.constraints.Min;

public record OpenBankingWithdrawRequest(Long payMemberId, Long toTrustAccountId,
                                         Long fromBankAccountId,
                                         @Min(1) Integer amount) {

}
