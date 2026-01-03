package com.zm.zx.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    NORMAL(1),
    LOCKED(0);
    private Integer status;

}
