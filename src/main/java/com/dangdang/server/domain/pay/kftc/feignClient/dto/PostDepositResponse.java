package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import java.util.List;

public record PostDepositResponse(
    String api_tran_id,
    String rsp_code,
    String rsp_message,
    String api_tran_dtm,
    String wd_bank_code_std,
    String wd_bank_code_sub,
    String wd_bank_name,
    String wd_account_num_masked,
    String wd_print_content,
    String wd_account_holder_name,
    String res_cnt,
    List<PostDepositResList> res_list) {

}