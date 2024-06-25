package com.arextest.common.beans;

import com.arextest.common.filter.ContentFilter;
import com.arextest.common.interceptor.MetricInterceptor;
import com.arextest.common.metrics.MetricConfig;
import com.arextest.common.metrics.MetricListener;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class that registers and sets up the ContentCachingFilter interceptor.
 * created by xinyuan_wang on 2023/12/25
 */
@Configuration
public class ContentInterceptorConfiguration implements WebMvcConfigurer {

  private final MetricInterceptor metricInterceptor;
  private final List<MetricListener> metricListeners;
  private final MetricConfig metricConfig;

  public ContentInterceptorConfiguration(MetricInterceptor metricInterceptor,
      List<MetricListener> metricListeners,
      MetricConfig metricConfig) {
    this.metricInterceptor = metricInterceptor;
    this.metricListeners = metricListeners;
    this.metricConfig = metricConfig;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    if (CollectionUtils.isEmpty(metricListeners)) {
      return;
    }
    registry.addInterceptor(metricInterceptor);
  }

  /**
   * Register the ContentCachingFilter filter and add it to the Filter chain
   */
  @Bean
  public FilterRegistrationBean<ContentFilter> contentCachingFilter() {
    FilterRegistrationBean<ContentFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new ContentFilter(metricListeners, metricConfig));
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }
}
