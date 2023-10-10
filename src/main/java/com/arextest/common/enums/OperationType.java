package com.arextest.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wildeslam.
 * @create 2023/10/9 15:54
 */
@AllArgsConstructor
public enum OperationType {
    NORMAL(1)
    ;

    @Getter
    private final Integer code;

}
