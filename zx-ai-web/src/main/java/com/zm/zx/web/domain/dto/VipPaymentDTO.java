package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * VIP支付 DTO
 */
@Data
public class VipPaymentDTO {
    
    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    
    /**
     * 支付密码
     */
    @NotBlank(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须为6位数字")
    private String paymentPassword;
}

