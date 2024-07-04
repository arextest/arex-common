package com.arextest.common.config;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author niyan
 * @date 2024/6/6
 * @since 1.0.0
 */
@Component
@Slf4j
public class DefaultApplicationConfig extends AbstractConfig {

    @Resource
    private ConfigProvider configProvider;

    @Override
    public String getConfigAsString(String key) {
        return configProvider.getConfigAsString(key);
    }
}
