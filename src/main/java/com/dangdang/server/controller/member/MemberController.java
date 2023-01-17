package com.dangdang.server.controller.member;

import com.dangdang.server.domain.member.application.MemberService;
import com.dangdang.server.domain.member.application.SmsMessageService;
import com.dangdang.server.domain.member.dto.request.MemberCertifiyRequestDto;
import com.dangdang.server.domain.member.dto.request.MemberSendRequestDto;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.response.MemberSignUpResponse;
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
  private final MemberService memberService;

  public MemberController(SmsMessageService smsMessageService, MemberService memberService) {
    this.smsMessageService = smsMessageService;
    this.memberService = memberService;
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
    memberService.certify(memberCertifiyRequestDto.getToNumber(),
        memberCertifiyRequestDto.getRandomNumber());

    return ResponseEntity.ok(null);
  }

  @PostMapping("/signup")
  public ResponseEntity<MemberSignUpResponse> signup(
      @RequestBody MemberSignUpRequest memberSignUpRequest) {
    MemberSignUpResponse memberSignUpResponse = memberService.signup(memberSignUpRequest);
    return ResponseEntity.ok(memberSignUpResponse);
  }
}
