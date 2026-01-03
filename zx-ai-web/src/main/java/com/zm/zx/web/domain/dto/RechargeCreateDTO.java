package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 创建充值订单DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargeCreateDTO {
    
    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 支付方式：1-支付宝
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;
}

