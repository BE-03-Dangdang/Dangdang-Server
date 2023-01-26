package com.dangdang.server.global.util;

import com.dangdang.server.global.config.S3Config;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StaticContextInitializer {

  private S3Config s3Config;
  private ApplicationContext applicationContext;

  @Autowired
  public StaticContextInitializer(S3Config s3Config, ApplicationContext applicationContext) {
    this.s3Config = s3Config;
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  public void init() {
    S3ImageUtil.setS3Config(s3Config);
  }

}
