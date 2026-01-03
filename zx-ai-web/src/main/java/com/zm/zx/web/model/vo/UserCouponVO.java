package com.zm.zx.web.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCouponVO {
    
    private Long userCouponId;
    private Long couponId;
    private String name;
    private Integer type; // 1-满减券，2-折扣券
    private String typeName;
    private BigDecimal discountAmount; // 满减金额
    private BigDecimal discountRate; // 折扣率（如0.8表示8折）
    private BigDecimal minAmount; // 最低消费金额
    private BigDecimal maxDiscount; // 最大优惠金额（折扣券用）
    private LocalDateTime expireTime;
    private String description;
    private Boolean canUse; // 是否可用于当前订单
    private String unusableReason; // 不可用原因
}

