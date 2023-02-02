package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveRequest;
import lombok.Getter;

@Getter
public class PostReceiveReqeust extends RequestDtoHelper {

  private String bank_tran_id;
  private String cntr_account_type;
  private String cntr_account_num;        // 신탁계좌
  private String bank_code_std;
  private String account_num;       // 수취 확인 입금 계좌
  private String account_seq;
  private String print_content;
  private String tran_amt;      // 입금 요청 금액
  private String req_client_name;
  private String req_client_bank_code;
  private String req_client_account_num;
  private String req_client_num;
  private String transfer_purpose;

  public PostReceiveReqeust(String cntr_account_num,
      OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest) {
    this.bank_tran_id = "M202300123U" + makeBankTranId();
    this.cntr_account_type = "N";
    this.transfer_purpose = "TR";
    this.cntr_account_num = cntr_account_num;
    this.bank_code_std = openBankingInquiryReceiveRequest.bankCode();
    this.account_num = openBankingInquiryReceiveRequest.bankAccountNumber();
    this.account_seq = String.valueOf((int) (Math.random() * 1000));
    this.print_content = "당당페이";
    this.tran_amt = openBankingInquiryReceiveRequest.depositAmount().toString();
    this.req_client_name = openBankingInquiryReceiveRequest.accountHolder();
    this.req_client_bank_code = openBankingInquiryReceiveRequest.bankCode();
    this.req_client_account_num = openBankingInquiryReceiveRequest.bankAccountNumber();
    this.req_client_num = makeBankTranId();
  }
}
