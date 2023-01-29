package com.dangdang.server.controller.pay;

import com.dangdang.server.domain.member.application.MemberService;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.response.MemberCertifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestHelper {

  @Autowired
  MemberService memberService;
  @Autowired
  RedisAuthCodeRepository redisAuthCodeRepository;

  protected String jwtSetUp() {
    String phoneNumber = "1";
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("천호동", phoneNumber, "url",
        "닉네임");
    redisAuthCodeRepository.save(new RedisAuthCode(phoneNumber));

    MemberCertifyResponse signup = memberService.signup(memberSignUpRequest);
    return "Bearer " + signup.getAccessToken();
  }

}
