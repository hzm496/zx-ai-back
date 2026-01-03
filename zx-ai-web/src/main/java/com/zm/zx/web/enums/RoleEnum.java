package com.zm.zx.web.enums;

import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    USER("common_user", "普通用户"),
    ADMIN("admin", "管理员");
    
    private String role;
    private String description;

    /**
     * 根据角色字符串获取枚举，找不到时抛出异常
     */
    public static RoleEnum getValue(String role) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.getRole().equals(role)) {
                return value;
            }
        }
        throw new BizException(ResponseEnum.NO_AUTH);
    }
    
    /**
     * 检查角色字符串是否有效
     */
    public static boolean isValid(String role) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
