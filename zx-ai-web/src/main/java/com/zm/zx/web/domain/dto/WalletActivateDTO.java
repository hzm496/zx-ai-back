package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 开通钱包 DTO
 */
@Data
public class WalletActivateDTO {
    
    /**
     * 支付密码（6位数字）
     */
    @NotBlank(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须为6位数字")
    private String paymentPassword;
    
    /**
     * 确认支付密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 支付宝账号
     */
    @NotBlank(message = "支付宝账号不能为空")
    private String alipayAccount;
    
    /**
     * 支付宝账户姓名
     */
    @NotBlank(message = "支付宝账户姓名不能为空")
    private String alipayName;
}

