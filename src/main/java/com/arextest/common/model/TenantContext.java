package com.arextest.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@NoArgsConstructor
public class TenantContext {

  private String tenantCode;
  private String serviceName;

  public TenantContext(TenantContext other) {
    this.tenantCode = other.tenantCode;
    this.serviceName = other.serviceName;
  }

  public TenantContext deepClone() {
    return new TenantContext(this);
  }
}