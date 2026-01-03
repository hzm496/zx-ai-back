package com.zm.zx.common.exception;

import cn.dev33.satoken.exception.*;
import com.zm.zx.common.response.Response;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(BizException.class)
    public Response<?> handleBizException(BizException e) {
        return Response.fail(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return Response.fail(e.getMessage());
    }

    /**
     * 处理 @Validated 校验异常 (用于 @RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Response.fail(errorMsg);
    }

    /**
     * 处理 @Validated 校验异常 (用于 @RequestParam 和表单)
     */
    @ExceptionHandler(BindException.class)
    public Response<?> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Response.fail(errorMsg);
    }

    /**
     * 处理 Sa-Token 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Response<?> handleNotLoginException(NotLoginException e) {
        String message;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = "未提供token";
                break;
            case NotLoginException.INVALID_TOKEN:
                message = "token无效";
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = "token已过期";
                break;
            case NotLoginException.BE_REPLACED:
                message = "账号已在其他设备登录";
                break;
            case NotLoginException.KICK_OUT:
                message = "账号已被踢下线";
                break;
            default:
                message = "当前会话未登录";
                break;
        }
        return Response.fail("401", message);
    }

    /**
     * 处理 Sa-Token 权限不足异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Response<?> handleNotPermissionException(NotPermissionException e) {
        return Response.fail("403", "无此权限：" + e.getPermission());
    }

    /**
     * 处理 Sa-Token 角色不足异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Response<?> handleNotRoleException(NotRoleException e) {
        return Response.fail("403", "无此角色：" + e.getRole());
    }

    /**
     * 处理 Sa-Token 服务被封禁异常
     */
    @ExceptionHandler(DisableServiceException.class)
    public Response<?> handleDisableServiceException(DisableServiceException e) {
        return Response.fail("403", "当前账号已被封禁，服务：" + e.getService() + "，级别：" + e.getLevel());
    }

    /**
     * 处理 Sa-Token 二级认证异常
     */
    @ExceptionHandler(NotSafeException.class)
    public Response<?> handleNotSafeException(NotSafeException e) {
        return Response.fail("401", "二级认证未通过");
    }

    /**
     * 处理 Sa-Token 基础异常
     */
    @ExceptionHandler(SaTokenException.class)
    public Response<?> handleSaTokenException(SaTokenException e) {
        return Response.fail("500", "认证异常：" + e.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        e.printStackTrace();
        return Response.fail("系统异常：" + e.getMessage());
    }
}