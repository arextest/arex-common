package com.arextest.common.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author niyan
 * @date 2024/6/14
 * @since 1.0.0
 */
public abstract class AbstractConfig {

  protected abstract String getConfigAsString(String key);

  public String getConfigAsString(String key, String defaultValue) {
    return StringUtils.defaultString(getConfigAsString(key), defaultValue);
  }

  public int getConfigAsInt(String key, int defaultValue) {
    return NumberUtils.toInt(getConfigAsString(key), defaultValue);
  }

  public int getConfigAsInt(String key) {
    return NumberUtils.toInt(getConfigAsString(key));
  }

  public long getConfigAsLong(String key, long defaultValue) {
    return NumberUtils.toLong(getConfigAsString(key), defaultValue);
  }

  public boolean getConfigAsBoolean(String key, boolean defaultValue) {
    String value = getConfigAsString(key);
    if (StringUtils.isBlank(value)) {
      return defaultValue;
    }

    return Boolean.parseBoolean(value);
  }

}
