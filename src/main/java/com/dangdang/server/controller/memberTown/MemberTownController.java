package com.dangdang.server.controller.memberTown;

import com.dangdang.server.domain.memberTown.application.MemberTownService;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownCertifyRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownCertifyResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownRangeResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownResponse;
import com.dangdang.server.global.aop.CurrentUserId;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member-town")
public class MemberTownController {

  private final MemberTownService memberTownService;

  public MemberTownController(MemberTownService memberTownService) {
    this.memberTownService = memberTownService;
  }

  @CurrentUserId
  @PostMapping
  public ResponseEntity<MemberTownResponse> createMemberTown(
      @RequestBody @Valid MemberTownRequest memberTownRequest, Long memberId) {
    MemberTownResponse memberTownSaveResponse = memberTownService.createMemberTown(
        memberTownRequest, memberId);
    return ResponseEntity.ok(memberTownSaveResponse);
  }

  @CurrentUserId
  @DeleteMapping
  public void deleteMemberTown(
      @RequestBody @Valid MemberTownRequest memberTownRequest, Long memberId) {
    memberTownService.deleteMemberTown(memberTownRequest, memberId);
  }

  @CurrentUserId
  @PutMapping("/active")
  public ResponseEntity<MemberTownResponse> changeActiveMemberTown(
      @RequestBody @Valid MemberTownRequest memberTownRequest, Long memberId) {
    MemberTownResponse memberTownResponse = memberTownService
        .changeActiveMemberTown(memberTownRequest, memberId);
    return ResponseEntity.ok(memberTownResponse);
  }

  @CurrentUserId
  @PutMapping("/range")
  public ResponseEntity<MemberTownRangeResponse> changeMemberTownRange(
      @RequestBody @Valid MemberTownRangeRequest memberTownRangeRequest, Long memberId) {
    MemberTownRangeResponse memberTownRangeResponse = memberTownService
        .changeMemberTownRange(memberTownRangeRequest, memberId);

    return ResponseEntity.ok(memberTownRangeResponse);
  }

  @CurrentUserId
  @PostMapping("/certification")
  public ResponseEntity<MemberTownCertifyResponse> certifyMemberTown(@RequestBody
  MemberTownCertifyRequest memberTownCertifyRequest, Long memberId) {
    MemberTownCertifyResponse memberTownCertifyResponse = memberTownService
        .certifyMemberTown(memberTownCertifyRequest, memberId);
    return ResponseEntity.ok(memberTownCertifyResponse);
  }
}
