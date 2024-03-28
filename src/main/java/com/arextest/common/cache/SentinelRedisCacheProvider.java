package com.arextest.common.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * redis://masterName:password@192.168.0.1:6379,192.168.0.2:6379,192.168.0.3:6379/0
 */
@Slf4j
public class SentinelRedisCacheProvider extends AbstractRedisCacheProvider {

  private final static String USERNAME_AND_PASSWORD_REGEX = "redis://(.*?)@(.*?)";

  private final static String DATABASE_REGEX = "redis://(.*?)/(.*?)";

  public SentinelRedisCacheProvider() {
    jedisPool = new JedisPool();
  }

  public SentinelRedisCacheProvider(String redisHost) {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(1024);
    jedisPoolConfig.setMaxIdle(100);
    jedisPoolConfig.setMaxWaitMillis(100);
    jedisPoolConfig.setTestOnBorrow(false);
    jedisPoolConfig.setTestOnReturn(true);

    Pair<String, String> userNameAndPassword = getUserNameAndPassword(redisHost);
    String masterName = userNameAndPassword.getLeft();
    String password =
        StringUtils.isEmpty(userNameAndPassword.getRight()) ? null : userNameAndPassword.getRight();
    if (StringUtils.isEmpty(masterName)) {
      throw new RuntimeException("the master name is empty");
    }

    List<String> redisHostAndPort = getRedisHostAndPort(redisHost);
    if (CollectionUtils.isEmpty(redisHostAndPort)) {
      throw new RuntimeException("the redis host and port is empty");
    }

    Integer dataBase = getDataBase(redisHost);
    int targetDataBase = dataBase != null ? dataBase : 0;

    jedisPool = new JedisSentinelPool(masterName, new HashSet<>(redisHostAndPort),
        jedisPoolConfig, 2000, password, targetDataBase);
    redissonClient = createRedissonClientByAnalyze(userNameAndPassword, redisHostAndPort,
        targetDataBase);
  }


  private Pair<String, String> getUserNameAndPassword(String redisUri) {
    String user = "";
    String password = "";
    Pattern pattern = Pattern.compile(USERNAME_AND_PASSWORD_REGEX);
    Matcher matcher = pattern.matcher(redisUri);
    if (matcher.matches()) {
      String group = matcher.group(1);
      String[] split = group.split(":");
      int len = split.length;
      if (len > 0) {
        user = split[0];
      }
      if (len > 1) {
        password = split[1];
      }
    }
    return new MutablePair<>(user, password);
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
    result.addAll(Arrays.asList(hostAndPortArr));
    return result;
  }

  private RedissonClient createRedissonClientByAnalyze(Pair<String, String> userNameAndPassword,
      List<String> redisHostAndPort, Integer dataBase) {

    String masterName = userNameAndPassword.getLeft();
    String password = userNameAndPassword.getRight();
    Config config = new Config();
    SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
    sentinelServersConfig.setMasterName(masterName)
        .setDatabase(dataBase)
        .setCheckSentinelsList(false);
    if (StringUtils.isNotEmpty(password)) {
      sentinelServersConfig.setPassword(password);
    }
    for (String item : redisHostAndPort) {
      sentinelServersConfig.addSentinelAddress("redis://" + item);
    }
    return Redisson.create(config);
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


}
