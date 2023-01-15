package com.dangdang.server.global.utill;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {

  private final StringRedisTemplate stringRedisTemplate;

  public RedisUtil(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public String getData(String key) {
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    return valueOperations.get(key);
  }

  public void setDataExpire(String key, String value, long duration) {
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    Duration expireDuration = Duration.ofSeconds(duration);
    valueOperations.set(key, value, expireDuration);
  }

  public void deleteData(String key) {
    stringRedisTemplate.delete(key);
  }
}
