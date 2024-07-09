package com.arextest.common.configuration;

import com.arextest.common.config.ConfigProvider;
import com.arextest.common.config.DefaultConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CommonAutoConfiguration
 *
 * @author xinyuan_wang
 * @date 2024/6/28 13:32
 */
@Configuration
@Slf4j
public class DefaultConfiguration {

    @Bean
    @ConditionalOnMissingBean(ConfigProvider.class)
    public ConfigProvider defaultConfigProvider() {
        return new DefaultConfigProvider();
    }

}
