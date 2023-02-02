package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import lombok.Getter;

@Getter
public class PostWithdrawRequest extends BankTranIdMaker {

  private String cntr_account_type;
  private String transfer_purpose;
  private String bank_tran_id;  // 이용기관 코드(M202300123) + U + 난수 9자리(4B332018Z)
  private String cntr_account_num;    // 입금 약정 계좌 번호
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

  public PostWithdrawRequest(String cntr_account_num, String dps_print_content,
      String fintech_use_num, String req_client_fintech_use_num, String tran_amt, String tran_dtime,
      String req_client_name, String req_client_num, String recv_client_name,
      String recv_client_bank_code, String recv_client_account_num) {
    this.cntr_account_type = "N";
    this.transfer_purpose = "TR";
    this.bank_tran_id = "M202300123U" + makeBankTranId();
    this.cntr_account_num = cntr_account_num;
    this.dps_print_content = dps_print_content;
    this.fintech_use_num = fintech_use_num;
    this.req_client_fintech_use_num = req_client_fintech_use_num;
    this.tran_amt = tran_amt;
    this.tran_dtime = tran_dtime;
    this.req_client_name = req_client_name;
    this.req_client_num = req_client_num;
    this.recv_client_name = recv_client_name;
    this.recv_client_bank_code = recv_client_bank_code;
    this.recv_client_account_num = recv_client_account_num;
  }
}
