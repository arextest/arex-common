package com.arextest.common.exceptions;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author b_yu
 * @since 2023/11/8
 */
public class ArexException extends RuntimeException {

  @Getter
  protected final int responseCode;

  public ArexException(int responseCode, String message) {
    super(message);
    this.responseCode = responseCode;
  }
}
