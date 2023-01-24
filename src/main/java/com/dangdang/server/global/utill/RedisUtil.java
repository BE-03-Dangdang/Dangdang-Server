package com.dangdang.server.global.utill;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisUtil {

  private static StringRedisTemplate stringRedisTemplate;

  public RedisUtil(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public static String getData(String key) {
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    return valueOperations.get(key);
  }

  public static void setDataExpire(String key, String value, long duration) {
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    Duration expireDuration = Duration.ofSeconds(duration);
    valueOperations.set(key, value, expireDuration);
  }

  public static void deleteData(String key) {
    stringRedisTemplate.delete(key);
  }
}
