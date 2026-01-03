package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityVO {
    
    /**
     * 活动ID
     */
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
     * 活动类型：1-送会员，2-送优惠券
     */
    private Integer type;
    
    /**
     * 活动类型名称
     */
    private String typeName;
    
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
     * 已领取次数
     */
    private Integer receiveCount;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 活动状态：1-未开始，2-进行中，3-已结束
     */
    private Integer activityStatus;
    
    /**
     * 活动状态名称
     */
    private String activityStatusName;
    
    // ===== 奖励信息 =====
    
    /**
     * 奖励ID
     */
    private Long rewardId;
    
    /**
     * 奖励类型：1-会员，2-优惠券
     */
    private Integer rewardType;
    
    /**
     * VIP时长（天）
     */
    private Integer vipDuration;
    
    /**
     * VIP时长描述
     */
    private String vipDurationName;
    
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}



