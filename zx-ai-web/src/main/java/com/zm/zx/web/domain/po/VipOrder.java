package com.zm.zx.web.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VIP订单表
 * @TableName vip_order
 */
@TableName(value = "vip_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VipOrder {
    /**
     * 订单ID
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
     * 支付方式：1-余额支付，2-支付宝支付
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

