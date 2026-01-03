package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/web/coupon")
@RequiredArgsConstructor
@Slf4j
public class CouponController {
    
    private final CouponService couponService;
    
    /**
     * 查询用户可用于指定课程的优惠券
     */
    @ApiOperationLog(description = "查询可用优惠券")
    @SaCheckLogin
    @GetMapping("/available")
    public Response getAvailableCoupons(@RequestParam("courseId") Long courseId, 
                                        @RequestParam("coursePrice") BigDecimal coursePrice) {
        return couponService.getAvailableCouponsForCourse(courseId, coursePrice);
    }
    
    /**
     * 计算使用优惠券后的价格
     */
    @ApiOperationLog(description = "计算优惠价格")
    @SaCheckLogin
    @GetMapping("/calculate")
    public Response calculateDiscountedPrice(@RequestParam("coursePrice") BigDecimal coursePrice,
                                             @RequestParam("userCouponId") Long userCouponId) {
        return couponService.calculateDiscountedPrice(coursePrice, userCouponId);
    }
}

