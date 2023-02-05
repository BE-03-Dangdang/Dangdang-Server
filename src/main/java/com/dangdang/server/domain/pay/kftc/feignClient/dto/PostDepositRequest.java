package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.domain.BankType;
import java.util.List;
import lombok.Getter;

@Getter
public class PostDepositRequest extends RequestDtoHelper {

  String cntr_account_type;
  String cntr_account_num;    // 출금될 신탁계좌
  String wd_pass_phrase;
  String wd_print_content;    // 출금계좌 인자내역
  String name_check_option;
  String tran_dtime;
  String req_cnt;
  List<ReqList> req_list;

  public PostDepositRequest(OpenBankingDepositRequest openBankingDepositRequest) {
    this.cntr_account_type = "N";
    this.cntr_account_num = openBankingDepositRequest.fromTrustAccountNumber();
    this.wd_pass_phrase = "NONE";
    this.wd_print_content = openBankingDepositRequest.payMemberName() + " 송금";
    this.name_check_option = "off";
    this.tran_dtime = makeTranDtime();
    this.req_cnt = "1";

    BankType depositBankType = BankType.from(openBankingDepositRequest.toBankName());
    BankType requestClientBankType = BankType.from(
        openBankingDepositRequest.payMemberConnectionAccountBank());
    ReqList requestOnlyOne = new ReqList("M202300123U" + makeBankTranId(),
        depositBankType.getBankCode(),
        openBankingDepositRequest.toBankAccountNumber(),
        openBankingDepositRequest.payMemberName(), openBankingDepositRequest.fintechUseNum(),
        openBankingDepositRequest.payMemberName(), openBankingDepositRequest.amount().toString(),
        openBankingDepositRequest.payMemberName(), requestClientBankType.getBankCode(),
        openBankingDepositRequest.payMemberConnectionAccountNumber(), makeBankTranId());

    this.req_list = List.of(requestOnlyOne);
  }
}

@Getter
class ReqList {

  String tran_no;
  String bank_tran_id;
  String bank_code_std;
  String account_num;
  String account_holder_name;
  String fintech_use_num;
  String print_content;
  String tran_amt;
  String req_client_name;
  String req_client_bank_code;
  String req_client_account_num;
  String req_client_num;
  String transfer_purpose;

  public ReqList(String bank_tran_id, String bank_code_std, String account_num,
      String account_holder_name, String fintech_use_num, String print_content, String tran_amt,
      String req_client_name, String req_client_bank_code, String req_client_account_num,
      String req_client_num) {
    this.tran_no = "1";     // 거래순번
    this.bank_tran_id = bank_tran_id;   // 이용기관 코드(M202300123) + U + 난수 9자리(4B332018Z)
    this.bank_code_std = bank_code_std;     // 입금계좌 은행 코드
    this.account_num = account_num;     // 입금계좌번호
    this.account_holder_name = account_holder_name;       // 입금계좌 예금주명
    this.fintech_use_num = fintech_use_num;     // 핀테크 이용번호
    this.print_content = print_content;     // 입금 계좌 인자내역
    this.tran_amt = tran_amt;       // 입금 요청 금액
    this.req_client_name = req_client_name;
    this.req_client_bank_code = req_client_bank_code;     // 요청 고객 연결계좌 은행 코드
    this.req_client_account_num = req_client_account_num;
    this.req_client_num = req_client_num;
    this.transfer_purpose = "TR";
  }
}