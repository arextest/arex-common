package com.arextest.common.interceptor;

import java.util.List;
import org.springframework.web.servlet.HandlerInterceptor;

public abstract class AbstractInterceptorHandler implements HandlerInterceptor {
  public abstract Integer getOrder();
  public abstract List<String> getPathPatterns();
  public abstract List<String> getExcludePathPatterns();
}
