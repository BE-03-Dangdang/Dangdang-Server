package com.dangdang.server.global.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dangdang.server.global.util.S3ImageUtil;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class S3ImageUtilTest {

  @Autowired
  S3ImageUtil s3ImageUtil;

  private boolean isValidUrl(String url) throws MalformedURLException {
    try {
      new URL(url);
      return true;
    } catch (MalformedURLException e) {
      return false;
    }
  }

  @Test
  @DisplayName("유효한 이미지 링크를 생성한다.")
  public void getValidImageLink() throws Exception {
    //given
    String url = "https://dangdang-server.s3.ap-northeast-2.amazonaws.com/post-image/test2.png";
    // when
    String s3Url = s3ImageUtil.makeImageLink(url);
    //then
    assertTrue(isValidUrl(s3Url));
  }
}