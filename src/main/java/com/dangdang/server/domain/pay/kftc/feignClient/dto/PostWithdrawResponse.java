package com.dangdang.server.domain.pay.kftc.feignClient.dto;

public record PostWithdrawResponse(
    String api_tran_id,
    String rsp_code,
    String rsp_message,
    String api_tran_dtm,
    String dps_bank_code_std,
    String dps_bank_code_sub,
    String dps_bank_name,
    String dps_account_num_masked,
    String dps_print_content,
    String dps_account_holder_name,
    String bank_tran_id,
    String bank_tran_date,
    String bank_code_tran,
    String bank_rsp_code,
    String bank_rsp_message,
    String fintech_use_num,
    String account_alias,
    String bank_code_std,
    String bank_code_sub,
    String bank_name,
    String account_num_masked,
    String print_content,
    String tran_amt,
    String account_holder_name,
    String wd_limit_remain_amt,
    String savings_bank_name) {

}
