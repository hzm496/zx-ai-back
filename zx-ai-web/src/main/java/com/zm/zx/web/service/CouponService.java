package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

import java.math.BigDecimal;

/**
 * 优惠券服务接口
 */
public interface CouponService {
    
    /**
     * 查询用户可用的优惠券（用于课程购买）
     * @param courseId 课程ID
     * @param coursePrice 课程价格
     * @return 可用优惠券列表
     */
    Response getAvailableCouponsForCourse(Long courseId, BigDecimal coursePrice);
    
    /**
     * 计算使用优惠券后的价格
     * @param coursePrice 原价
     * @param userCouponId 用户优惠券ID
     * @return 优惠后价格信息
     */
    Response calculateDiscountedPrice(BigDecimal coursePrice, Long userCouponId);
}

