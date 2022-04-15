package io.arex.common.model.response;


/**
 * @author jmo
 * @since 2021/11/3
 */
public interface Response {
    ResponseStatusType getResponseStatusType();

    void setResponseStatusType(ResponseStatusType responseStatusType);
}
