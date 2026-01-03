package com.zm.zx.web.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程订单实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("course_order")
public class CourseOrder {

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
     * 优惠券ID（用户优惠券ID）
     */
    private Long couponId;
    
    /**
     * 优惠金额
     */
    private BigDecimal couponAmount;

    /**
     * 支付方式：1-余额，2-支付宝
     */
    private Integer payType;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付宝交易号
     */
    private String alipayTradeNo;

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
}

