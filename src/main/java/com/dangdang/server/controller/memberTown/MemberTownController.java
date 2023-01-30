package com.dangdang.server.controller.memberTown;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.application.MemberTownService;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownRangeResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

  @PostMapping
  public ResponseEntity<MemberTownResponse> createMemberTown(
      @RequestBody @Valid MemberTownRequest memberTownRequest,
      Authentication authentication) {
    MemberTownResponse memberTownSaveResponse = memberTownService.createMemberTown(memberTownRequest,
        (Member) authentication.getPrincipal());
    return ResponseEntity.ok(memberTownSaveResponse);
  }

  @DeleteMapping
  public void deleteMemberTown(
      @RequestBody @Valid MemberTownRequest memberTownRequest,
      Authentication authentication) {
     memberTownService.deleteMemberTown(memberTownRequest, (Member) authentication.getPrincipal());
  }

  @PutMapping("/active")
  public ResponseEntity<MemberTownResponse> changeActiveMemberTown(
      @RequestBody @Valid MemberTownRequest memberTownRequest,
      Authentication authentication) {
    MemberTownResponse memberTownResponse = memberTownService.changeActiveMemberTown(
        memberTownRequest,
        (Member) authentication.getPrincipal());
    return ResponseEntity.ok(memberTownResponse);
  }

  @PutMapping("/range")
  public ResponseEntity<MemberTownRangeResponse> changeMemberTownRange(
      @RequestBody @Valid MemberTownRangeRequest memberTownRangeRequest,
      Authentication authentication) {
    MemberTownRangeResponse memberTownRangeResponse = memberTownService
        .changeMemberTownRange(memberTownRangeRequest, (Member) authentication.getPrincipal());

    return ResponseEntity.ok(memberTownRangeResponse);
  }
}
