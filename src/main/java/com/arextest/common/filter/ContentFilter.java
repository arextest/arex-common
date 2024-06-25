package com.arextest.common.filter;

import static com.arextest.common.metrics.MetricConfig.*;
import com.arextest.common.metrics.MetricConfig;
import com.arextest.common.metrics.MetricListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
    String clientApp = httpServletRequest.getHeader(SERVICE_NAME_HEADER);
    String category = httpServletRequest.getHeader(CATEGORY_TYPE_HEADER);
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
    putIfValueNotEmpty(clientApp, SERVICE_NAME, tags);
    putIfValueNotEmpty(category, CATEGORY, tags);
    putIfValueNotEmpty(path, PATH, tags);
    for (MetricListener metricListener : metricListeners) {
      tags.put(TYPE, REQUEST_TAG);
      metricListener.recordSize(ENTRY_PAYLOAD_NAME, tags, requestLength);
      tags.put(TYPE, RESPONSE_TAG);
      metricListener.recordSize(ENTRY_PAYLOAD_NAME, tags, responseLength);
    }
  }

  @Override
  public void destroy() {
    // do nothing
  }
}
