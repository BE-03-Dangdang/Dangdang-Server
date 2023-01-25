package com.dangdang.server.global.util;

import static com.dangdang.server.global.exception.ExceptionCode.IMAGE_URL_INVALID;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.dangdang.server.global.config.S3Config;
import com.dangdang.server.global.exception.UrlInValidException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class S3ImageUtil {

  private static S3Config s3Config;
  private static AmazonS3Client amazonS3Client;
  private static String bucketName;
  private static final long EXPIRED_TIME = 3600000L;

  public static void setS3Config(S3Config s3Config) {
    S3ImageUtil.s3Config = s3Config;
    S3ImageUtil.amazonS3Client = s3Config.amazonS3Client();
    S3ImageUtil.bucketName = s3Config.getBucketName();
  }


  public static String makeImageLink(String imageUrl) {
    URL url = validateUrl(imageUrl);

    String filePath = url.getPath().substring(1);
    Date expiration = setExpiredTime();

    GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(
        filePath, expiration);

    return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest)
        .toString(); // 접근가능한 URL 가져오기
  }

  private static URL validateUrl(String imageUrl) {
    URL url;
    try {
      url = new URL(imageUrl);
    } catch (MalformedURLException e) {
      throw new UrlInValidException(IMAGE_URL_INVALID);
    }
    return url;
  }

  private static Date setExpiredTime() {
    Date expiration = new Date();
    long expTimeMillis = expiration.getTime();
    expTimeMillis += EXPIRED_TIME; // 1 hour
    expiration.setTime(expTimeMillis);
    return expiration;
  }

  private static GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String filePath,
      Date expiration) {
    GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
        bucketName, filePath.replace(File.separatorChar, '/')).withMethod(HttpMethod.GET)
        .withExpiration(expiration);
    return generatePresignedUrlRequest;
  }

}
