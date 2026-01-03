package com.zm.zx.web.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动 VO（用户端）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityVO {
    
    private Long id;
    private String title;
    private String description;
    private Integer type;
    private String typeName;
    private String coverImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer limitPerUser;
    private Integer totalLimit;
    private Integer receiveCount;
    
    // 奖励信息
    private Integer rewardType;
    private Integer vipDuration;
    private String vipDurationName;
    private BigDecimal couponAmount;
    private BigDecimal couponMinAmount;
    private Integer couponExpireDays;
    
    // 用户领取状态
    private Boolean hasReceived; // 用户是否已领取
    private Integer userReceiveCount; // 用户已领取次数
    private Boolean canReceive; // 是否可以领取
}


