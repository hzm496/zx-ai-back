package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建提现申请DTO
 */
@Data
public class WithdrawCreateDTO {
    
    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "1.00", message = "提现金额最少为1元")
    @DecimalMax(value = "10000.00", message = "单笔提现金额最多为10000元")
    private BigDecimal amount;

    /**
     * 提现方式：1-支付宝，2-微信，3-银行卡（目前仅支持支付宝，使用钱包绑定的账号）
     */
    @NotNull(message = "提现方式不能为空")
    @Min(value = 1, message = "提现方式不正确")
    @Max(value = 3, message = "提现方式不正确")
    private Integer accountType;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}

