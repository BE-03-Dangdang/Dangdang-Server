package com.dangdang.server.domain.pay.kftc.openBankingFacade.dto;

public record OpenBankingInquiryReceiveRequest(
    Long payMemberId,
    String bankAccountNumber,
    String bankCode
) {

}
