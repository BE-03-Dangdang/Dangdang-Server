package com.dangdang.server.domain.pay.kftc;

import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetUserMeResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingInquiryReceiveRequest;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingInquiryReceiveResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingWithdrawRequest;
import org.springframework.stereotype.Service;

@Service
public interface OpenBankingService {

  /**
   * 가입 (토큰 발급 전)
   */
  void createOpenBankingMemberFromState(String state, PayMember payMember);

  /**
   * AccessToken 발급
   */
  GetAuthTokenResponse getAuthToken(GetAuthTokenRequest getAuthTokenRequest);

  GetUserMeResponse getUserMeResponse(String token, String user_seq_no);

  /**
   * 입금 이체
   */
  OpenBankingResponse deposit(OpenBankingDepositRequest openBankingDepositRequest);

  /**
   * 출금 이체
   */
  OpenBankingResponse withdraw(OpenBankingWithdrawRequest openBankingWithdrawRequest);

  /**
   * 수취조회
   */
  OpenBankingInquiryReceiveResponse inquiryReceive(
      OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest);
}
