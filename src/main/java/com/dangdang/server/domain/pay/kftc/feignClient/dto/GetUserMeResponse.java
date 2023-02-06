package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record GetUserMeResponse(
    String api_tran_id,
    String rsp_code,
    String rsp_message,
    String api_tran_dtm,
    String user_seq_no,
    String user_ci,
    String user_name,
    String res_cnt,
    List<PostDepositResList> res_list,
    String inquiry_card_cnt,
    List inquiry_card_list,
    String inquiry_pay_cnt,
    List inquiry_pay_list,
    String inquiry_insurance_cnt,
    List inquiry_insurance_list,
    String inquiry_loan_cnt,
    List inquiry_loan_list) {

}

@Getter
@AllArgsConstructor
class ResList {

  String fintech_use_num;
  String account_alias;
  String bank_code_std;
  String bank_code_sub;
  String bank_name;
  String account_num_masked;
  String account_holder_name;
  String account_holder_type;
  String inquiry_agree_yn;
  String inquiry_agree_dtime;
  String transfer_agree_yn;
  String transfer_agree_dtime;
  String payer_num;
  String savings_bank_name;
  String account_seq;
  String account_type;

}
