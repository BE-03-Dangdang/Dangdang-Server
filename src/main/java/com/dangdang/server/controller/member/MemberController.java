package com.dangdang.server.controller.member;

import com.dangdang.server.domain.member.application.MemberService;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.member.dto.response.MemberCertifyResponse;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping("/signup")
  public ResponseEntity<MemberCertifyResponse> signUp(@RequestBody MemberSignUpRequest memberSignupRequest) {
    MemberCertifyResponse memberCertifyResponse = memberService.signup(memberSignupRequest);
    return ResponseEntity.ok(memberCertifyResponse);
  }

  @PostMapping("/signupCertify")
  public ResponseEntity<MemberCertifyResponse> signupCertify(
      @RequestBody @Valid PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    MemberCertifyResponse memberCertifyResponse = memberService.signupCertify(phoneNumberCertifyRequest);

    return ResponseEntity.ok(memberCertifyResponse);
  }

  @PostMapping("/loginCertify")
  public ResponseEntity<Null> loginCertify(
      @RequestBody @Valid PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    memberService.loginCertify(phoneNumberCertifyRequest);

    return ResponseEntity.ok(null);
  }
}
