package com.zm.zx.web.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单表
 * @TableName recharge_order
 */
@TableName(value = "recharge_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargeOrder {
    /**
     * 充值订单ID
     */
    @TableId(type = IdType.AUTO)
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
     * 状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;

    /**
     * 支付宝交易号
     */
    private String alipayTradeNo;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

