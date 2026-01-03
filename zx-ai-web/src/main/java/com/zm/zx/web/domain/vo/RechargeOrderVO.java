package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargeOrderVO {
    
    /**
     * 充值订单ID
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
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式：1-支付宝
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
     * 支付宝交易号
     */
    private String alipayTradeNo;
    
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime payTime;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}

