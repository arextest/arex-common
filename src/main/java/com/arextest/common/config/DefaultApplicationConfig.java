package com.arextest.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author niyan
 * @date 2024/6/6
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultApplicationConfig extends AbstractConfig {

  private final ConfigProvider configProvider;

  @Override
  public String getConfigAsString(String key) {
    return configProvider.getConfigAsString(key);
  }
}
