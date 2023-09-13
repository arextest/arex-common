package com.arextest.common.metrics;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author b_yu
 * @since 2023/9/11
 */
@Slf4j
public class PrometheusConfiguration {
    public static void initMetrics(String port) {
        try {
            if (StringUtils.isBlank(port)) {
                LOGGER.info("prometheus port is blank");
                return;
            }
            DefaultExports.initialize();
            new HTTPServer.Builder().withPort(Integer.parseInt(port)).build();
        } catch (Exception e) {
            LOGGER.error("init prometheus error", e);
        }
    }
}
