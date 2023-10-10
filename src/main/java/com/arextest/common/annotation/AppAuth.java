package com.arextest.common.annotation;

import com.arextest.common.enums.AuthRejectStrategy;
import com.arextest.common.enums.OperationType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the call of the interface
 * needs to be authenticated on the dimension of the app.
 * @author wildeslam.
 * @create 2023/10/9 15:47
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppAuth {
    /**
     * Indicates what operation the interface performs.
     */
    OperationType op() default OperationType.NORMAL;

    /**
     * Indicates the process after authentication method.
     * <p>Default is {@code #AuthRejectStrategy.FAIL_RESPONSE}.
     */
    AuthRejectStrategy rejectStrategy() default AuthRejectStrategy.FAIL_RESPONSE;

}
