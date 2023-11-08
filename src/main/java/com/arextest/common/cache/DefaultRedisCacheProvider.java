package com.arextest.common.cache;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReplicatedServersConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author jmo
 * @since 2022/2/16
 */
@Slf4j
public class DefaultRedisCacheProvider implements CacheProvider {

  private static final String STATUS_CODE = "OK";
  private static final byte[] SET_IF_NOT_EXIST = "NX".getBytes(StandardCharsets.UTF_8);
  private static final byte[] SET_WITH_EXPIRE_TIME = "EX".getBytes(StandardCharsets.UTF_8);

  private final static String USERNAME_AND_PASSWORD_REGEX = "redis://(.*?)@(.*?)";
  private final static String DATABASE_REGEX = "redis://(.*?)/(.*?)";

  private RedissonClient redissonClient;

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
      redissonClient = redissonClient = createRedissonClientByAnalyze(redisHost);
    } catch (URISyntaxException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

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
  public LockWrapper getLock(String namespaceId) {
    return new RedissonLock(redissonClient.getLock(namespaceId));
  }

  private RedissonClient createRedissonClientByAnalyze(String redisUri) {
    Config config = new Config();
    ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers()
        .setScanInterval(2000);
    List<String> redisHostAndPort = getRedisHostAndPort(redisUri);
    replicatedServersConfig.addNodeAddress(redisHostAndPort.toArray(new String[0]));

    Pair<String, String> userNameAndPassword = getUserNameAndPassword(redisUri);
    String user = userNameAndPassword.getKey();
    String password = userNameAndPassword.getValue();
    if (StringUtils.isNotEmpty(user)) {
      replicatedServersConfig.setUsername(user);
    }
    if (StringUtils.isNotEmpty(password)) {
      replicatedServersConfig.setPassword(password);
    }

    Integer dataBase = getDataBase(redisUri);
    if (dataBase != null) {
      replicatedServersConfig.setDatabase(dataBase);
    }
    return Redisson.create(config);
  }

  private List<String> getRedisHostAndPort(String redisUri) {
    List<String> result = new ArrayList<>();
    String substring = null;
    if (redisUri.contains("@")) {
      int i = redisUri.indexOf('@');
      if (i != -1) {
        substring = redisUri.substring(i + 1);
      }
    } else {
      int i = redisUri.indexOf("redis://");
      if (i != -1) {
        substring = redisUri.substring(i + 7 + 1);
      }
    }

    if (substring == null) {
      return result;
    }
    String[] split = substring.split("/");
    String hostAndPortStr = split[0];
    String[] hostAndPortArr = hostAndPortStr.split(",");
    for (String hostAndPort : hostAndPortArr) {
      result.add("redis://" + hostAndPort);
    }
    return result;
  }

  private Pair<String, String> getUserNameAndPassword(String redisUri) {
    String user = "";
    String password = "";
    Pattern pattern = Pattern.compile(USERNAME_AND_PASSWORD_REGEX);
    Matcher matcher = pattern.matcher(redisUri);
    if (matcher.matches()) {
      String group = matcher.group(1);
      String[] split = group.split(":");
      user = split[0];
      password = split[1];
    }
    return new MutablePair<>(user, password);
  }

  private Integer getDataBase(String redisUri) {
    Integer database = null;
    Pattern pattern = Pattern.compile(DATABASE_REGEX);
    Matcher matcher = pattern.matcher(redisUri);
    if (matcher.matches()) {
      String group = matcher.group(2);
      if (StringUtils.isNotBlank(group)) {
        database = Integer.valueOf(group);
      }
    }
    return database;
  }

  private boolean statusCode2Boolean(String statusCode) {
    return STATUS_CODE.equals(statusCode);
  }

  private boolean integer2Boolean(long l) {
    return l > 0;
  }
}
