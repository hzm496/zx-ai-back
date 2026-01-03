package com.zm.zx.common.model.record.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户活动领取记录表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user_activity_record")
public class UserActivityRecord {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 奖励类型：1-会员，2-优惠券
     */
    private Integer rewardType;
    
    /**
     * 获得的VIP时长（天）
     */
    private Integer vipDuration;
    
    /**
     * 获得的优惠券ID（user_coupon表）
     */
    private Long couponId;
    
    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

