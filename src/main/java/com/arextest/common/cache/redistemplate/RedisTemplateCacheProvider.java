package com.arextest.common.cache.redistemplate;

import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateCacheProvider extends AbstractRedisTemplateProvider {

  public RedisTemplateCacheProvider(RedisTemplate redisTemplate, RedissonClient redissonClient) {
    super(redisTemplate, redissonClient);
  }

}
