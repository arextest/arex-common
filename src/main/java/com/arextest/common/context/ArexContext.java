package com.arextest.common.context;

import lombok.Getter;
import lombok.Setter;

/**
 * Thread local context.
 * @author wildeslam.
 * @create 2023/10/9 19:42
 */
public class ArexContext {
    private static final ThreadLocal<ArexContext> LOCAL = new ThreadLocal<>();

    @Setter
    @Getter
    private String appId;

    @Setter
    @Getter
    private String operator;

    @Setter
    @Getter
    private Boolean passAuth;


    public static ArexContext getContext() {
        if (LOCAL.get() == null) {
            LOCAL.set(new ArexContext());
        }
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }
}
