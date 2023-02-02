package com.dangdang.server.domain.pay.kftc.openBankingFacade.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;

/**
 * 오픈뱅킹 API는 핀테크이용번호만 보내면 계좌번호를 알아서 매칭시켜 준다.
 */
@Entity
@Getter
public class FintechUseNum {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fintech_use_num_id")
  private Long id;

  @Column
  private String fintech_use_num;

  @Column
  private String bankAccountNumber;

  protected FintechUseNum() {
  }

}
