package com.zm.zx.common.enums;

import com.zm.zx.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseEnum implements BaseExceptionInterface {
    SUCCESS("200", "成功"),
    FAIL("500", "失败"),

    /*无权限*/
    NO_AUTH("4001", "无访问权限"),
    /*账号或密码错误*/
    USER_PASSWORD_ERROR("4002", "账号或密码错误"),
    /*用户不存在*/
    USER_NOT_EXIST("4003", "用户不存在"),
    /*账号已冻结*/
    USER_FROZEN("4004", "账号已冻结"),
    /*用户已存在*/
    USER_EXIST("4005", "用户已存在"),
    /*用户信息更新失败*/
    USER_UPDATE_FAIL("4006", "用户信息更新失败"),
    /*两次密码不一致*/
    PASSWORD_NOT_MATCH("4007", "两次密码不一致"),
    /*旧密码不正确*/
    OLD_PASSWORD_ERROR("4008", "旧密码不正确"),
    //修改密码失败
    PASSWORD_UPDATE_FAIL("4009", "修改密码失败"),
    /*删除失败*/
    DELETE_FAIL("4010", "删除失败"),
    /*修改失败*/
    UPDATE_FAIL("4011", "修改失败"),
    /*添加教师失败*/
    ADD_FAIL("4012", "添加教师失败"),
    /*邮件未找到*/
    DATA_NOT_FOUND("4013", "邮件未找到" );
    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}