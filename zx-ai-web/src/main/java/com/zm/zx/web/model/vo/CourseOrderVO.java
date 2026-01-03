package com.zm.zx.web.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程订单 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseOrderVO {

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
    private String username;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程标题
     */
    private String courseTitle;

    /**
     * 课程封面
     */
    private String courseCover;

    /**
     * 课程原价
     */
    private BigDecimal originalPrice;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付方式：1-余额，2-支付宝
     */
    private Integer payType;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;

    /**
     * 订单状态名称
     */
    private String statusName;

    /**
     * 支付宝交易号
     */
    private String alipayTradeNo;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否可以支付（前端按钮显示）
     */
    private Boolean canPay;

    /**
     * 是否可以取消（前端按钮显示）
     */
    private Boolean canCancel;
}
