package com.dangdang.server.domain.pay.kftc.feignClient.domain;

import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;

@Entity
@Getter
public class OpenBankingMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "open_banking_member_id")
  private Long id;

  @Column(unique = true)
  private String code;

  @Column(length = 500)
  private String accessToken;

  @Column(length = 500)
  private String refreshToken;

  private String userSeqNo;

  @Column(nullable = false, unique = true)
  private String state;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  protected OpenBankingMember() {
  }

  public OpenBankingMember(String state, PayMember payMember) {
    this.state = state;
    this.payMember = payMember;
  }

  public OpenBankingMember(String code) {
    this.code = code;
  }

  public OpenBankingMember(String accessToken, String refreshToken, String userSeqNo) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.userSeqNo = userSeqNo;
  }

  public void updateTokenAndSeqNo(OpenBankingMember updateOpenBankingMember) {
    this.accessToken = updateOpenBankingMember.accessToken;
    this.refreshToken = updateOpenBankingMember.refreshToken;
    this.userSeqNo = updateOpenBankingMember.userSeqNo;
  }

  public void updateStateAndPayMember(OpenBankingMember updateOpenBankingMember) {
    this.state = updateOpenBankingMember.getState();
    this.payMember = updateOpenBankingMember.getPayMember();
  }

  public void updateCode(OpenBankingMember updateOpenBankingMember) {
    this.code = updateOpenBankingMember.code;
  }
}
