package com.arextest.common.interceptor;

import com.arextest.common.metrics.MetricConfig;
import com.arextest.common.metrics.MetricListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * record request and response interceptor
 * created by xinyuan_wang on 2023/12/25
 */
@Component
public class MetricInterceptor implements HandlerInterceptor {
  public final List<MetricListener> metricListeners;
  public final MetricConfig metricConfig;

  public MetricInterceptor(List<MetricListener> metricListeners, MetricConfig metricConfig) {
    this.metricListeners = metricListeners;
    this.metricConfig = metricConfig;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    request.setAttribute(MetricConfig.START_TIME, System.currentTimeMillis());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws IOException {
    Object startTime = request.getAttribute(MetricConfig.START_TIME);
    if (startTime == null) {
      return;
    }

    long executeMillis =  System.currentTimeMillis() - (long) startTime;

    recordExecuteMillis(request.getHeader(MetricConfig.SERVICE_NAME_HEADER), request.getHeader(MetricConfig.CATEGORY_TYPE_HEADER),
        request.getRequestURI(), request.getMethod(), executeMillis);
  }

  public void recordExecuteMillis(String serviceName, String category,
      String path, String method, long executeMillis) {
    if (CollectionUtils.isEmpty(metricListeners) || metricConfig.skipMetric(method)) {
      return;
    }

    Map<String, String> tags = new HashMap<>();
    metricConfig.putIfValueNotEmpty(serviceName, MetricConfig.SERVICE_NAME, tags);
    metricConfig.putIfValueNotEmpty(category, MetricConfig.CATEGORY, tags);
    metricConfig.putIfValueNotEmpty(path, MetricConfig.PATH, tags);
    for (MetricListener metricListener : metricListeners) {
      if (executeMillis > 0) {
        metricListener.recordTime(MetricConfig.ENTRY_PAYLOAD_NAME, tags, executeMillis);
      }
    }
  }
}
