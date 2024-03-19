package com.arextest.common.interceptor;

import java.util.List;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public abstract class AbstractInterceptorHandler extends HandlerInterceptorAdapter {


  public abstract Integer getOrder();

  public abstract List<String> getPathPatterns();
  public abstract List<String> getExcludePathPatterns();

}
