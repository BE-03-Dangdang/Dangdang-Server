package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
import lombok.Getter;

@Getter
public class PostWithdrawRequest extends RequestDtoHelper {

  private String cntr_account_type;
  private String transfer_purpose;
  private String bank_tran_id;  // 이용기관 코드(M202300123) + U + 난수 9자리(4B332018Z)
  private String cntr_account_num;    // 약정 계좌 번호 (출금이체할 계좌)
  private String dps_print_content;
  private String fintech_use_num;   // 출금 계좌 핀테크 이용번호
  private String req_client_fintech_use_num;
  private String tran_amt;    // 거래금액
  private String tran_dtime;
  private String req_client_name;
  private String req_client_num;
  private String recv_client_name;
  private String recv_client_bank_code;
  private String recv_client_account_num;

  protected PostWithdrawRequest() {
  }

  public PostWithdrawRequest(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    this.bank_tran_id = "M202300123U" + makeBankTranId();
    this.cntr_account_type = "N";
    this.transfer_purpose = "TR";
    this.cntr_account_num = openBankingWithdrawRequest.fromBankAccountNumber();
    this.dps_print_content = "당당페이";
    this.fintech_use_num = openBankingWithdrawRequest.fintechUseNum();
    this.req_client_fintech_use_num = openBankingWithdrawRequest.fintechUseNum();
    this.tran_amt = openBankingWithdrawRequest.amount().toString();
    this.tran_dtime = makeTranDtime();
    this.req_client_name = openBankingWithdrawRequest.accountHolder();
    this.req_client_num = makeBankTranId();
    this.recv_client_name = "당당페이";
    this.recv_client_bank_code = "088";
    this.recv_client_account_num = "34";
  }
}
