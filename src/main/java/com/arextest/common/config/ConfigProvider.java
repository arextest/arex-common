package com.arextest.common.config;

import java.util.Map;

/**
 * @author niyan
 * @date 2024/6/6
 * @since 1.0.0
 */
public interface ConfigProvider {
    /**
     * load all configs
     */
    void loadConfigs(String configName);

    /**
     * replace all configs
     */
    void onChange(Map<String, String> configs);

    /**
     * get config as string
     */
    String getConfigAsString(String key);
}
