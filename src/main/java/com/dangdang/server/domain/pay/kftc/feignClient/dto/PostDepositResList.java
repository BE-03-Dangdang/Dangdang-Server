package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import lombok.Getter;

@Getter
public class PostDepositResList {

  String tran_no;
  String bank_tran_id;
  String bank_tran_date;
  String bank_code_tran;
  String bank_rsp_code;
  String bank_rsp_message;
  String bank_code_std;
  String bank_code_sub;
  String bank_name;
  String account_num;
  String account_num_masked;
  String print_content;
  String tran_amt;
  String account_holder_name;
  String cms_num;
  String savings_bank_name;
  String account_seq;
  String withdraw_bank_tran_id;
}
