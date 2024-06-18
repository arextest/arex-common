package com.arextest.common.interceptor;

import static com.arextest.common.utils.MetricUtils.*;
import com.arextest.common.metrics.MetricListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  public MetricInterceptor(List<MetricListener> metricListeners) {
    this.metricListeners = metricListeners;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    request.setAttribute(START_TIME, System.currentTimeMillis());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws IOException {
    Object startTime = request.getAttribute(START_TIME);
    if (startTime == null) {
      return;
    }

    long executeMillis =  System.currentTimeMillis() - (long) startTime;

    recordExecuteMillis(request.getHeader(SERVICE_NAME_HEADER), request.getHeader(CATEGORY_TYPE_HEADER),
        request.getRequestURI(), executeMillis);
  }

  public void recordExecuteMillis(String serviceName, String category,
      String path, long executeMillis) {
    if (CollectionUtils.isEmpty(metricListeners)) {
      return;
    }

    Map<String, String> tags = new HashMap<>();
    putIfValueNotEmpty(serviceName, SERVICE_NAME, tags);
    putIfValueNotEmpty(category, CATEGORY, tags);
    putIfValueNotEmpty(path, PATH, tags);
    for (MetricListener metricListener : metricListeners) {
      if (executeMillis > 0) {
        metricListener.recordTime(ENTRY_PAYLOAD_NAME, tags, executeMillis);
      }
    }
  }
}
