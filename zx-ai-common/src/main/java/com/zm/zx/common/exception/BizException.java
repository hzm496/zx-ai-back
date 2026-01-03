package com.zm.zx.common.exception;

import lombok.Data;

@Data
public class BizException extends RuntimeException {
    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;
 
    public BizException(BaseExceptionInterface baseExceptionInterface) {
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }
    
    /**
     * 自定义错误信息（使用默认错误码 500）
     */
    public BizException(String errorMessage) {
        this.errorCode = "500";
        this.errorMessage = errorMessage;
    }
    
    /**
     * 自定义错误码和错误信息
     */
    public BizException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}