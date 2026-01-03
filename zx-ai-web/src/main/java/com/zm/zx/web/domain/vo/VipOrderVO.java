package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VIP订单 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VipOrderVO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 套餐类型：1-月卡，2-季卡，3-年卡
     */
    private Integer packageType;
    
    /**
     * 套餐名称
     */
    private String packageName;
    
    /**
     * 时长（天）
     */
    private Integer duration;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 支付方式：1-余额支付
     */
    private Integer paymentMethod;
    
    /**
     * 支付方式名称
     */
    private String paymentMethodName;
    
    /**
     * 状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

