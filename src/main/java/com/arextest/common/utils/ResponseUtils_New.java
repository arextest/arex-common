package com.arextest.common.utils;

import com.arextest.common.model.response.GenericResponseType;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.model.response.ResponseCode_New;
import com.arextest.common.model.response.ResponseStatusType;

/**
 * @author b_yu
 * @since 2023/11/8
 */
public class ResponseUtils_New {
  private static final String REQUESTED_BODY_EMPTY = "requested body empty";
  private static final String SUCCESS = "success";

  private ResponseUtils_New() {

  }

  public static Response exceptionResponse(String remark) {
    return errorResponse(remark, ResponseCode_New.REQUESTED_HANDLE_EXCEPTION);
  }

  public static Response errorResponse(String remark, int responseCode) {
    return errorResponse(responseStatus(remark, responseCode));
  }

  public static Response errorResponse(ResponseStatusType responseStatusType) {
    return create(responseStatusType, null);
  }

  public static <T> Response successResponse(T body) {
    return create(successStatus(), body);
  }

  public static <T> Response create(ResponseStatusType statusType, T body) {
    return new GenericResponseType<>(statusType, body);
  }

  public static Response resourceNotFoundResponse() {
    return errorResponse(ResponseCode.REQUESTED_RESOURCE_NOT_FOUND.name(),
        ResponseCode_New.REQUESTED_RESOURCE_NOT_FOUND);
  }

  public static Response parameterInvalidResponse(String remark) {
    return errorResponse(remark, ResponseCode_New.REQUESTED_PARAMETER_INVALID);
  }

  public static ResponseStatusType successStatus() {
    return responseStatus(SUCCESS, ResponseCode_New.SUCCESS);
  }

  public static Response requestBodyEmptyResponse() {
    return parameterInvalidResponse(REQUESTED_BODY_EMPTY);
  }

  private static ResponseStatusType responseStatus(String remark, int responseCode) {
    ResponseStatusType responseStatusType = new ResponseStatusType();
    responseStatusType.setResponseDesc(remark);
    responseStatusType.setResponseCode(responseCode);
    responseStatusType.setTimestamp(System.currentTimeMillis());
    return responseStatusType;
  }
}
