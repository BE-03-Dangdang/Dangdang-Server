package com.dangdang.server.domain.pay.kftc.feignClient;

import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetUserMeResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostReceiveReqeust;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostReceiveResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostWithdrawRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.PostWithdrawResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// 오픈API 공식 문서 : https://developers.kftc.or.kr/dev/tool/open-banking
@FeignClient(name = "openAPI", url = "https://testapi.openbanking.or.kr")
public interface OpenApiFeignClient {

  /**
   * 사용자 인증
   */
  @PostMapping(value = "/oauth/2.0/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  GetAuthTokenResponse getAuthorizeToken(@RequestParam("code") String code,
      @RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret,
      @RequestParam("redirect_uri") String redirectUri,
      @RequestParam("grant_type") String grantType);

  /**
   * 사용자 정보 조회
   */
  @GetMapping(value = "/v2.0/user/me")
  GetUserMeResponse getUserInfo(@RequestHeader("Authorization") String token,
      @RequestParam String user_seq_no);

  /**
   * 출금 이체
   */
  @PostMapping(value = "/v2.0/transfer/withdraw/fin_num", produces = "application/json")
  PostWithdrawResponse withdraw(@RequestHeader("Authorization") String token, @RequestBody
  PostWithdrawRequest postWithdrawRequest);

  /**
   * 수취 조회
   */
  @PostMapping(value = "/v2.0/inquiry/receive", produces = "application/json")
  PostReceiveResponse inquiryReceive(@RequestHeader("Authorization") String openBankingToken,
      @RequestBody PostReceiveReqeust postReceiveReqeust);
}
