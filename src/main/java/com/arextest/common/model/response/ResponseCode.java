package com.arextest.common.model.response;


import lombok.Getter;

/**
 * @author jmo
 * @since 2021/11/8
 */

public enum ResponseCode {
    /**
     * means everything is ok
     */
    SUCCESS(0),
    /**
     * means the parameter of request value validate failed.
     */
    REQUESTED_PARAMETER_INVALID(1),
    /**
     * means complete the request occurred internal processing exception.
     */
    REQUESTED_HANDLE_EXCEPTION(2),
    /**
     * means complete the request required resources not found.
     */
    REQUESTED_RESOURCE_NOT_FOUND(3),
    /**
     * means authentication verification failed
     */
    AUTHENTICATION_FAILED(4);

    @Getter
    private final int codeValue;

    ResponseCode(int codeValue) {
        this.codeValue = codeValue;
    }
}
