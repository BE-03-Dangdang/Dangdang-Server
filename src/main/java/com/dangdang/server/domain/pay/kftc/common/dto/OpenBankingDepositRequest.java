package com.dangdang.server.domain.pay.kftc.common.dto;

public record OpenBankingDepositRequest(Long payMemberId, String openBankingToken,
                                        String toBankAccountNumber,
                                        String fromTurstAccountNumber,
                                        Integer amount) {

}
