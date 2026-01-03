package com.zm.zx.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VIP订单 VO（管理后台）
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
     * 用户名
     */
    private String username;
    
    /**
     * 套餐类型：1-月卡，2-季卡，3-年卡
     */
    private Integer packageType;
    
    /**
     * 套餐名称
     */
    private String packageName;
    
    /**
     * VIP名称（用于前端显示）
     */
    private String vipName;
    
    /**
     * 时长（天）
     */
    private Integer duration;
    
    /**
     * 原价
     */
    private BigDecimal originalAmount;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 实付金额（价格）
     */
    private BigDecimal payAmount;
    
    /**
     * 支付方式：1-余额支付，2-支付宝支付
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
    
    /**
     * 退款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;
    
    /**
     * 退款原因
     */
    private String refundReason;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

