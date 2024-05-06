package com.arextest.common.model;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class TenantContext {

  private String tenantCode;
}