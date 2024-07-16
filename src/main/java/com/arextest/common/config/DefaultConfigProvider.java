package com.arextest.common.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * @author niyan
 * @date 2024/6/6
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultConfigProvider implements ConfigProvider {

  private final Environment environment;

  @Override
  public void loadConfigs(String configName) {
    // nothing to do
  }

  @Override
  public void onChange(Map<String, String> configs) {
    // nothing to do
  }


  @Override
  public String getConfigAsString(String key) {
    return environment.getProperty(key, String.class, StringUtils.EMPTY);
  }
}
