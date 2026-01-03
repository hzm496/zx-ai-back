package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 添加活动 DTO
 */
@Data
public class ActivityAddDTO {
    
    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    private String title;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动类型：1-送会员，2-送优惠券
     */
    @NotNull(message = "活动类型不能为空")
    private Integer type;
    
    /**
     * 活动封面图
     */
    private String coverImage;
    
    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    /**
     * 每人限领次数
     */
    private Integer limitPerUser;
    
    /**
     * 活动总限额
     */
    private Integer totalLimit;
    
    // ===== 奖励配置 =====
    
    /**
     * VIP时长（天），type=1时必填
     */
    private Integer vipDuration;
    
    /**
     * 优惠券金额，type=2时必填
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



