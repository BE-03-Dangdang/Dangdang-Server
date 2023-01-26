package com.dangdang.server.global.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 모든 경로에 대하여
    long MAX_AGE_SECS = 3600;
    registry.addMapping("/**")
        // Origin이 http:localhost:3000에 대해.
        .allowedOrigins("http://localhost:3000")
        // GET, POST, PUT, PATCH, DELETE, OPTIONS 메서드를 허용한다.
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(MAX_AGE_SECS);
  }
}
