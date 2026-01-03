package com.zm.zx.common.model.activity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("activity")
public class Activity {
    
    /**
     * 活动ID
     */
    @TableId(type = IdType.AUTO)
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
     * 每人限领次数，0表示不限制
     */
    private Integer limitPerUser;
    
    /**
     * 活动总限额，0表示不限制
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
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

