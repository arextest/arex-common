package com.arextest.common.cache;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author jmo
 * @since 2022/2/16
 */
@Slf4j
public final class DefaultRedisCacheProvider implements CacheProvider {
    private final static String STATUS_CODE = "OK";
    private JedisPool jedisPool;

    public DefaultRedisCacheProvider() {
        jedisPool = new JedisPool();
    }

    public DefaultRedisCacheProvider(String redisHost) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1024);
        jedisPoolConfig.setMaxIdle(100);
        jedisPoolConfig.setMaxWaitMillis(100);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(true);
        try {
            jedisPool = new JedisPool(jedisPoolConfig, new URI(redisHost));
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean put(byte[] key, long expiredSeconds, byte[] value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return statusCode2Boolean(jedis.setex(key, (int) expiredSeconds, value));
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
    public long decrValue(byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
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

    private boolean statusCode2Boolean(String statusCode) {
        return STATUS_CODE.equals(statusCode);
    }

    private boolean integer2Boolean(long l) {
        return l > 0;
    }
}
