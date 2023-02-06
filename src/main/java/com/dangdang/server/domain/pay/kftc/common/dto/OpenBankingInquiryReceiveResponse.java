package com.dangdang.server.domain.pay.kftc.common.dto;

import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostReceiveResponse;
import java.time.LocalDateTime;

public record OpenBankingInquiryReceiveResponse(Long payMemberId, String bankName,
                                                String receiveClientName,
                                                String accountNumber,
                                                LocalDateTime createdAt) {

  public static OpenBankingInquiryReceiveResponse of(Long payMemberId,
      PostReceiveResponse postReceiveResponse,
      OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest, LocalDateTime createdAt) {

    return new OpenBankingInquiryReceiveResponse(payMemberId,
        openBankingInquiryReceiveRequest.requestClientBankName(),
        postReceiveResponse.account_holder_name(),
        openBankingInquiryReceiveRequest.depositBankAccountNumber(), createdAt);
  }
}
