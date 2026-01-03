package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 处理提现申请DTO
 */
@Data
public class WithdrawProcessDTO {
    
    /**
     * 提现订单ID
     */
    @NotNull(message = "提现订单ID不能为空")
    private Long id;
    
    /**
     * 处理状态：1-通过，2-拒绝
     */
    @NotNull(message = "处理状态不能为空")
    private Integer status;
    
    /**
     * 拒绝原因（status=2时必填）
     */
    private String rejectReason;
    
    /**
     * 备注
     */
    private String remark;
}

