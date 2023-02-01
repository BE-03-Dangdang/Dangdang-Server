package com.dangdang.server.global.aop;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.global.security.JwtTokenProvider;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class CurrentUserIdAop {

  private static final String MEMBER_ID = "memberId";
  private final JwtTokenProvider jwtTokenProvider;

  public CurrentUserIdAop(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Around("@annotation(currentUserId)")
  public Object getCurrentUserId(ProceedingJoinPoint proceedingJoinPoint,
      CurrentUserId currentUserId) throws Throwable {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();

    String token = jwtTokenProvider.resolveAccessToken(request);
    token = jwtTokenProvider.bearerRemove(token);
    Authentication authentication = jwtTokenProvider.getAccessTokenAuthentication(token);
    Long memberId = ((Member) authentication.getPrincipal()).getId();

    Object[] modifiedArgs = modifyArgsWithMemberId(memberId, proceedingJoinPoint);
    return proceedingJoinPoint.proceed(modifiedArgs);
  }

  private Object[] modifyArgsWithMemberId(long memberId, ProceedingJoinPoint proceedingJoinPoint) {
    Object[] parameters = proceedingJoinPoint.getArgs();

    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = signature.getMethod();
    Parameter[] methodParameters = method.getParameters();

    for (int i = 0; i < methodParameters.length; i++) {
      String parameterName = methodParameters[i].getName();
      if (parameterName.equals(MEMBER_ID)) {
        parameters[i] = memberId;
      }
    }
    return parameters;
  }
}
