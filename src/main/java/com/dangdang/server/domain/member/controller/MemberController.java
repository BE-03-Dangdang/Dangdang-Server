package com.dangdang.server.domain.member.controller;

import com.dangdang.server.domain.member.dto.request.MemberCertifiyRequestDto;
import com.dangdang.server.domain.member.dto.request.MemberSendRequestDto;
import com.dangdang.server.domain.member.service.MemberCertifiedService;
import com.dangdang.server.domain.member.service.SmsMessageService;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

  private final SmsMessageService smsMessageService;
  private final MemberCertifiedService memberCertifiedService;

  public MemberController(SmsMessageService smsMessageService,
      MemberCertifiedService memberCertifiedService) {
    this.smsMessageService = smsMessageService;
    this.memberCertifiedService = memberCertifiedService;
  }

  @PostMapping("/send")
  public ResponseEntity<Null> send(
      @RequestBody @Valid MemberSendRequestDto memberSendRequestDto) {
    smsMessageService.sendMessage(memberSendRequestDto.getToNumber());

    return ResponseEntity.ok(null);
  }

  @PostMapping("/certify")
  public ResponseEntity<Null> certify(
      @RequestBody @Valid MemberCertifiyRequestDto memberCertifiyRequestDto) {
    memberCertifiedService.certify(memberCertifiyRequestDto.getToNumber(),
        memberCertifiyRequestDto.getRandomNumber());

    return ResponseEntity.ok(null);
  }
}
