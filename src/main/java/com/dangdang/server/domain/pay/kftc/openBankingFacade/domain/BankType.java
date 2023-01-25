package com.dangdang.server.domain.pay.kftc.openBankingFacade.domain;

import static com.dangdang.server.global.exception.ExceptionCode.BANK_TYPE_NOT_FOUND;

import com.dangdang.server.domain.pay.kftc.openBankingFacade.exception.BankTypeNotFoundException;
import java.util.Arrays;

/**
 * 오픈API 실제 금융기관 코드 (은행만 존재한다고 가정)
 */
public enum BankType {

  KDB("002", "KDB산업은행"),
  IBK("003", "IBK기업은행"),
  KB("004", "KB국민은행"),
  SH("007", "수협은행"),
  NH("011", "농협은행"),
  WORI("020", "우리은행"),
  SC("023", "SC제일은행"),
  CITY("027", "한국씨티은행"),
  DAEGU("031", "대구은행"),
  BUSAN("032", "부산은행"),
  KJ("034", "광주은행"),
  JEJU("035", "제주은행"),
  JB("037", "전북은행"),
  KN("039", "경남은행"),
  HANA("081", "하나은행"),
  SIN("088", "신한은행"),
  K("089", "케이뱅크"),
  KAKAO("090", "카카오뱅크");

  private String bankCode;
  private String bankName;

  BankType(String bankCode, String bankName) {
    this.bankCode = bankCode;
    this.bankName = bankName;
  }

  public static BankType from(String bankName) {
    return Arrays.stream(BankType.values())
        .filter(bankType -> bankType.getBankName().equals(bankName))
        .findFirst()
        .orElseThrow(() -> new BankTypeNotFoundException(BANK_TYPE_NOT_FOUND));
  }

  public String getBankName() {
    return bankName;
  }

  public String getBankCode() {
    return bankCode;
  }
}
