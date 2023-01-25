package com.dangdang.server.domain.pay.kftc.openBankingFacade.dto;

public record OpenBankingDepositRequest(Long payMemberId, Long toBankAccountId,
                                        Long fromTurstAccountId,
                                        Integer amount) {

}
