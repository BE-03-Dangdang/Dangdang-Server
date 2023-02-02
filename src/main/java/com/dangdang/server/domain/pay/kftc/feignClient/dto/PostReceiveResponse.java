package com.dangdang.server.domain.pay.kftc.feignClient.dto;

public record PostReceiveResponse(
    String api_tran_id,
    String api_tran_dtm,
    String rsp_code,
    String rsp_message,
    String bank_code_std,
    String bank_code_sub,
    String bank_name,
    String account_num,
    String account_num_masked,
    String print_content,
    String account_holder_name,
    String bank_tran_id,
    String bank_tran_date,
    String bank_code_tran,
    String bank_rsp_code,
    String bank_rsp_message,
    String wd_bank_code_std,
    String wd_bank_name,
    String wd_account_num,
    String tran_amt,
    String cms_num,
    String savings_bank_name,
    String account_seq) {

}
