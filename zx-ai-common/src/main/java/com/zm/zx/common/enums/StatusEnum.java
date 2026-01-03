package com.zm.zx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum {
    NORMAL(1),
    DISABLED(0);
    private Integer code;

}
