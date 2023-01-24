package com.dangdang.server.controller.memberTown;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.application.MemberTownService;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownSaveRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownSaveResponse;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memberTown")
public class MemberTownController {

  private final MemberTownService memberTownService;

  public MemberTownController(MemberTownService memberTownService) {
    this.memberTownService = memberTownService;
  }

  @PostMapping()
  public ResponseEntity<MemberTownSaveResponse> save(
      @RequestBody @Valid MemberTownSaveRequest memberTownSaveRequest,
      Authentication authentication) {
    MemberTownSaveResponse memberTownSaveResponse = memberTownService.save(memberTownSaveRequest,
        (Member) authentication.getPrincipal());
    return ResponseEntity.ok(memberTownSaveResponse);
  }

  @DeleteMapping("/{memberTownId}")
  public ResponseEntity<Void> delete(@PathVariable Long memberTownId,
      Authentication authentication) {
    memberTownService.delete(memberTownId, (Member) authentication.getPrincipal());
    return ResponseEntity.ok(null);
  }
}
