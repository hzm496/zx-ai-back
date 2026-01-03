package com.zm.zx.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    ABLE(1),
    DISABLE(0);
    private Integer code;

}
