package com.arextest.common.cache;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.redisson.api.RedissonClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

public class AbstractRedisCacheProvider implements CacheProvider {

  private static final String STATUS_CODE = "OK";
  RedissonClient redissonClient;
  Pool<Jedis> jedisPool;

  public static byte[] getRequestId() {
    return UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public boolean put(byte[] key, long expiredSeconds, byte[] value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return statusCode2Boolean(jedis.setex(key, (int) expiredSeconds, value));
    }
  }

  @Override
  public boolean put(byte[] key, byte[] value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return statusCode2Boolean(jedis.set(key, value));
    }
  }

  @Override
  public boolean putIfAbsent(byte[] key, long expiredSeconds, byte[] value) {
    try (Jedis jedis = jedisPool.getResource()) {
      boolean nxResult = integer2Boolean(jedis.setnx(key, value));
      if (nxResult) {
        this.expire(key, expiredSeconds);
      }
      return nxResult;
    }

  }

  @Override
  public byte[] get(byte[] key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.get(key);
    }
  }

  @Override
  public long incrValue(byte[] key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.incr(key);
    }
  }

  @Override
  public long incrValueBy(byte[] key, long value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.incrBy(key, value);
    }
  }

  @Override
  public long decrValue(byte[] key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.decr(key);
    }
  }

  @Override
  public long decrValueBy(byte[] key, long value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.decrBy(key, value);
    }
  }

  @Override
  public boolean remove(byte[] key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return integer2Boolean(jedis.del(key));
    }
  }

  @Override
  public boolean expire(byte[] key, long seconds) {
    try (Jedis jedis = jedisPool.getResource()) {
      return integer2Boolean(jedis.expire(key, (int) seconds));
    }
  }

  @Override
  public boolean exists(byte[] key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.exists(key);
    }
  }

  @Override
  public Long rpush(byte[] key, byte[]... args) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.rpush(key, args);
    }
  }

  @Override
  public List<byte[]> lrange(byte[] key, long start, long stop) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.lrange(key, start, stop);
    }
  }

  @Override
  public LockWrapper getLock(String namespaceId) {
    return new RedissonLock(redissonClient.getLock(namespaceId));
  }

  @Override
  public RedissonClient getRedissionClient() {
    return this.redissonClient;
  }

  private boolean statusCode2Boolean(String statusCode) {
    return STATUS_CODE.equals(statusCode);
  }

  private boolean integer2Boolean(long l) {
    return l > 0;
  }

}
