package com.arextest.common.runnable;

import com.arextest.common.model.TenantContext;
import com.arextest.common.utils.TenantContextUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.MDC;

@Slf4j
public abstract class AbstractContextWithTraceRunnable implements Runnable {

  private final Map<String, String> traceMap;
  private final TenantContext tenantContext;

  public AbstractContextWithTraceRunnable() {
    this.traceMap = MDC.getCopyOfContextMap();
    this.tenantContext = TenantContextUtil.getContext();
  }


  @Override
  public final void run() {
    Map<String, String> old = mark();
    TenantContext oldContext = markContext();
    try {
      doWithContextRunning();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      removeMark(old);
      removeContext(oldContext);
    }
  }

  protected abstract void doWithContextRunning();

  private Map<String, String> mark() {
    if (MapUtils.isEmpty(this.traceMap)) {
      return null;
    } else {
      Map<String, String> old = MDC.getCopyOfContextMap();
      MDC.setContextMap(this.traceMap);
      return old;
    }
  }

  private void removeMark(Map<String, String> prev) {
    if (MapUtils.isEmpty(prev)) {
      MDC.clear();
    } else {
      MDC.setContextMap(prev);
    }
  }

  private TenantContext markContext() {
    if (this.tenantContext == null) {
      return null;
    } else {
      TenantContext tenantContext = TenantContextUtil.getCopyOfContext();
      TenantContextUtil.setContext(this.tenantContext);
      return tenantContext;
    }
  }

  private void removeContext(TenantContext tenantContext) {
    if (tenantContext == null) {
      TenantContextUtil.clear();
    } else {
      TenantContextUtil.setContext(tenantContext);
    }
  }


}