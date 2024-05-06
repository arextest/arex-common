package com.arextest.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.arextest.common.model.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContextUtil {

  private static final TransmittableThreadLocal<TenantContext> LOCAL = new TransmittableThreadLocal<>();

  public static void setTenantCode(String tenantCode) {
    getOrCreateContext().setTenantCode(tenantCode);
  }

  public static String getTenantCode() {
    return getOrCreateContext().getTenantCode();
  }

  public static void setAll(TenantContext tenantContext) {
    LOCAL.set(tenantContext);
  }

  public static TenantContext getAll() {
    return LOCAL.get();
  }

  public static TenantContext getCopyOfAll() {
    TenantContext tenantContext = LOCAL.get();
    if (tenantContext == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(objectMapper.writeValueAsString(tenantContext), TenantContext.class);
    } catch (Exception e) {
      LOGGER.error("Failed to get copy of all", e);
      return null;
    }
  }

  public static void clearAll() {
    LOCAL.remove();
  }

  private static TenantContext getOrCreateContext() {
    if (LOCAL.get() == null) {
      LOCAL.set(new TenantContext());
    }
    return LOCAL.get();
  }

}
