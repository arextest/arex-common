package com.arextest.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.arextest.common.model.TenantContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContextUtil {

  private static final TransmittableThreadLocal<TenantContext> LOCAL = new TransmittableThreadLocal<>();

  public static void setTenantCode(String tenantCode) {
    getOrCreateContext().setTenantCode(tenantCode);
  }

  public static void setServiceName(String serviceName) {
    getOrCreateContext().setServiceName(serviceName);
  }

  public static String getTenantCode() {
    return getOrCreateContext().getTenantCode();
  }

  public static String getServiceName() {
    return getOrCreateContext().getServiceName();
  }

  public static void setContext(TenantContext tenantContext) {
    LOCAL.set(tenantContext);
  }

  public static TenantContext getContext() {
    return LOCAL.get();
  }

  public static TenantContext getCopyOfContext() {
    TenantContext tenantContext = LOCAL.get();
    if (tenantContext == null) {
      return null;
    }

    return tenantContext.deepClone();
  }

  public static void clear() {
    LOCAL.remove();
  }

  private static TenantContext getOrCreateContext() {
    if (LOCAL.get() == null) {
      LOCAL.set(new TenantContext());
    }
    return LOCAL.get();
  }

}
