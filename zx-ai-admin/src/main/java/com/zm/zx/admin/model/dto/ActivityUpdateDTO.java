package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新活动 DTO
 */
@Data
public class ActivityUpdateDTO {
    
    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long id;
    
    /**
     * 活动标题
     */
    private String title;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动封面图
     */
    private String coverImage;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 每人限领次数
     */
    private Integer limitPerUser;
    
    /**
     * 活动总限额
     */
    private Integer totalLimit;
    
    /**
     * 状态
     */
    private Integer status;
    
    // ===== 奖励配置 =====
    
    /**
     * VIP时长（天）
     */
    private Integer vipDuration;
    
    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;
    
    /**
     * 优惠券最低消费金额
     */
    private BigDecimal couponMinAmount;
    
    /**
     * 优惠券有效期（天）
     */
    private Integer couponExpireDays;
}



