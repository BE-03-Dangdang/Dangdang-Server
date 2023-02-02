package com.dangdang.server.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메소드에 해당 어노테이션을 부여하면 header의 accessToken를 활용하여 알아낸 memberId(type: Long)를 해당 어노테이션을 호출한 메소드의
 * 파라미터로 받아 활용할 수 있습니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CurrentUserId {

}
