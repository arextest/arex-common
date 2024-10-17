package com.arextest.common.filter;

import com.arextest.common.metrics.MetricConfig;
import com.arextest.common.metrics.MetricListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Cache the response and record the size of the request and response
 * created by xinyuan_wang on 2023/12/25
 */
@Slf4j
public class ContentFilter implements Filter {
  private final List<MetricListener> metricListeners;
  private final MetricConfig metricConfig;
  private static Method getContentMethod;
  private static AtomicBoolean methodInitialized = new AtomicBoolean(false);
  public ContentFilter(List<MetricListener> metricListeners, MetricConfig metricConfig) {
    this.metricListeners = metricListeners;
    this.metricConfig = metricConfig;
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // do nothing
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (CollectionUtils.isEmpty(metricListeners)) {
      chain.doFilter(request, response);
      return;
    }
    if (metricConfig.skipMetric(((HttpServletRequest) request).getMethod())) {
      chain.doFilter(request, response);
      return;
    }
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    chain.doFilter(httpServletRequest, response);
    logResponseSize(httpServletRequest, response);
  }

  private void logResponseSize(HttpServletRequest httpServletRequest,
      ServletResponse servletResponse) {
    String clientApp = httpServletRequest.getHeader(MetricConfig.SERVICE_NAME_HEADER);
    String category = httpServletRequest.getHeader(MetricConfig.CATEGORY_TYPE_HEADER);
    String requestURI = httpServletRequest.getRequestURI();
    long requestLength = httpServletRequest.getContentLengthLong();
    long responseLength = getResponseLength(servletResponse);
    recordPayloadInfo(clientApp, category, requestURI, requestLength, responseLength);
  }

  private long getResponseLength(ServletResponse servletResponse) {
    Class<? extends ServletResponse> responseClass = servletResponse.getClass();
    String responseClassName = responseClass.getName();
    if ("org.mortbay.jetty.Response".equals(responseClassName)) {
      // jetty
      return getResponseLengthByMethod(servletResponse, "getContentCount");
    } else if ("org.apache.catalina.connector.ResponseFacade".equals(responseClassName)) {
      //tomcat 7+
      return getResponseLengthByMethod(servletResponse, "getContentWritten");
    } else {
      return 0L;
    }
  }

  /**
   * The methodName will not change because when jetty or tomcat is started,
   * the underlying response type used is the same.
   * @param servletResponse
   * @param methodName
   * @return
   */
  private static long getResponseLengthByMethod(ServletResponse servletResponse, String methodName) {
    if (methodInitialized.get()) {
      return invokeMethod(servletResponse);
    }

    try {
      getContentMethod = servletResponse.getClass().getDeclaredMethod(methodName);
    } catch (NoSuchMethodException e) {
      LOGGER.error("failed to get declared method. {}", e.getMessage(), e);
    }
    methodInitialized.set(true);
    return invokeMethod(servletResponse);
  }

  private static long invokeMethod(ServletResponse servletResponse) {
    if (getContentMethod == null) {
      return 0L;
    }
    try {
      return (long) getContentMethod.invoke(servletResponse);
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.error("failed to log response size. {}", e.getMessage(), e);
    }
    return 0L;
  }

  private void recordPayloadInfo(String clientApp, String category,
      String path, long requestLength, long responseLength) {
    if (responseLength <= 0L || requestLength <= 0L) {
      return;
    }
    Map<String, String> tags = new HashMap<>();
    metricConfig.putIfValueNotEmpty(clientApp, MetricConfig.SERVICE_NAME, tags);
    metricConfig.putIfValueNotEmpty(category, MetricConfig.CATEGORY, tags);
    metricConfig.putIfValueNotEmpty(path, MetricConfig.PATH, tags);
    for (MetricListener metricListener : metricListeners) {
      tags.put(MetricConfig.TYPE, MetricConfig.REQUEST_TAG);
      metricListener.recordSize(MetricConfig.ENTRY_PAYLOAD_NAME, tags, requestLength);
      tags.put(MetricConfig.TYPE, MetricConfig.RESPONSE_TAG);
      metricListener.recordSize(MetricConfig.ENTRY_PAYLOAD_NAME, tags, responseLength);
    }
  }

  @Override
  public void destroy() {
    // do nothing
  }
}
