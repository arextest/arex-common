package com.arextest.common.cache.redistemplate;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.LockWrapper;
import com.arextest.common.cache.RedissonLock;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractRedisTemplateProvider implements CacheProvider {

  private final RedisTemplate<byte[], byte[]> byteRedisTemplate;

  private final RedissonClient redissonClient;

  @Override
  public boolean put(byte[] key, long expiredSeconds, byte[] value) {
    byteRedisTemplate.opsForValue().set(key, value, expiredSeconds, TimeUnit.SECONDS);
    return true;
  }

  @Override
  public boolean put(byte[] key, byte[] value) {
    byteRedisTemplate.opsForValue().set(key, value);
    return true;
  }

  @Override
  public boolean putIfAbsent(byte[] key, long expiredSeconds, byte[] value) {
    return byteRedisTemplate.opsForValue()
        .setIfAbsent(key, value, expiredSeconds, TimeUnit.SECONDS);
  }

  @Override
  public byte[] get(byte[] key) {
    return byteRedisTemplate.opsForValue().get(key);
  }

  @Override
  public long incrValue(byte[] key) {
    return byteRedisTemplate.opsForValue().increment(key);
  }

  @Override
  public long incrValueBy(byte[] key, long value) {
    return byteRedisTemplate.opsForValue().increment(key, value);
  }

  @Override
  public long decrValue(byte[] key) {
    return byteRedisTemplate.opsForValue().decrement(key);
  }

  @Override
  public long decrValueBy(byte[] key, long value) {
    return byteRedisTemplate.opsForValue().decrement(key, value);
  }

  @Override
  public boolean remove(byte[] key) {
    return byteRedisTemplate.delete(key);
  }

  @Override
  public boolean expire(byte[] key, long seconds) {
    return byteRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
  }

  @Override
  public boolean exists(byte[] key) {
    return byteRedisTemplate.hasKey(key);
  }

  @Override
  public LockWrapper getLock(String namespaceId) {
    return new RedissonLock(redissonClient.getLock(namespaceId));
  }

  @Override
  public RedissonClient getRedissionClient() {
    return redissonClient;
  }

}
