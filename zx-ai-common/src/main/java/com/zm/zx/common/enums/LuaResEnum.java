package com.zm.zx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LuaResEnum {
    ALREADY_PURCHASED(-1),
    //库存不足
    NOT_ENOUGH_STOCK(-2),
    //成功
    SUCCESS(1);
    private Integer code;

    public static LuaResEnum getByCode(Long code) {
        if (code == null) {
            return null;
        }
        int codeInt = code.intValue();
        for (LuaResEnum value : values()) {
            if (value.code.equals(codeInt)) {
                return value;
            }
        }
        return null;
    }
}
