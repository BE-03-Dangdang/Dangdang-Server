package com.dangdang.server.domain.pay.kftc.common.dto;

public record OpenBankingInquiryReceiveRequest(Long payMemberId, String openBankingToken,
                                               String requestClientBankName, String accountHolder,
                                               String requestClientBankAccountNumber,
                                               String depositBankName,
                                               String depositBankAccountNumber,
                                               Integer depositAmount) {

}
