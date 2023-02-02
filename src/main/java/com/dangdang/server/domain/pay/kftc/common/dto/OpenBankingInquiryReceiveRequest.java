package com.dangdang.server.domain.pay.kftc.common.dto;

public record OpenBankingInquiryReceiveRequest(Long payMemberId, String openBankingToken,
                                               String bankAccountNumber, String bankCode) {

}
