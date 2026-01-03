package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建VIP订单 DTO
 */
@Data
public class VipOrderCreateDTO {
    
    /**
     * 套餐类型：1-月卡，2-季卡，3-年卡
     */
    @NotNull(message = "套餐类型不能为空")
    private Integer packageType;
    
    /**
     * 支付方式：1-余额支付，2-支付宝支付
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;
}

