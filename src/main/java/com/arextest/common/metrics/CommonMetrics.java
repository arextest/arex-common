package com.arextest.common.metrics;

import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author b_yu
 * @since 2023/9/11
 */
@Slf4j
public class CommonMetrics {
    static Counter ERROR_COUNT =
            Counter.build()
                    .name("arextest_error_count")
                    .help("Total error count.")
                    .labelNames("project", "ip")
                    .register();

    public static void incErrorCount(String project, String ip) {
        try {
            ERROR_COUNT.labels(project, ip).inc();
        }catch (Throwable e){
            LOGGER.error("incErrorCount error", e);
        }
    }
}
