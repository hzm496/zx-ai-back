package com.zm.zx.common.model.activity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动奖励表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("activity_reward")
public class ActivityReward {
    
    /**
     * 奖励ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 奖励类型：1-会员，2-优惠券
     */
    private Integer rewardType;
    
    /**
     * VIP时长（天），奖励类型为会员时有效
     */
    private Integer vipDuration;
    
    /**
     * 优惠券金额，奖励类型为优惠券时有效
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

