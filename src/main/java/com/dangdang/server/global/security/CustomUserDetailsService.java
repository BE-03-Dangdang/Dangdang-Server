package com.dangdang.server.global.security;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  public CustomUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
    return memberRepository.findById(Long.parseLong(memberId)).orElseThrow(() -> new MemberNotFoundException(
        ExceptionCode.MEMBER_NOT_FOUND));
  }
}
