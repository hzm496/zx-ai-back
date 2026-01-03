package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.web.mapper.UserCouponMapper;
import com.zm.zx.web.mapper.WebCouponMapper;
import com.zm.zx.web.model.po.Coupon;
import com.zm.zx.web.model.po.UserCoupon;
import com.zm.zx.web.model.vo.UserCouponVO;
import com.zm.zx.web.service.CouponService;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    
    private final UserCouponMapper userCouponMapper;
    private final WebCouponMapper couponMapper;
    
    @Override
    public Response getAvailableCouponsForCourse(Long courseId, BigDecimal coursePrice) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 如果courseId为0或null，表示查询所有优惠券（用于个人中心）
        boolean queryAll = (courseId == null || courseId == 0);
        
        if (queryAll) {
            log.info("查询用户 {} 的所有有效优惠券", userId);
        } else {
            log.info("查询用户 {} 可用于课程 {} 的优惠券", userId, courseId);
        }
        
        // 1. 查询用户所有未使用的优惠券
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
            .eq(UserCoupon::getStatus, 0) // 未使用
            .gt(UserCoupon::getExpireTime, LocalDateTime.now()); // 未过期
        
        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);
        
        if (userCoupons.isEmpty()) {
            return Response.success(List.of());
        }
        
        // 2. 查询优惠券详情
        List<Long> couponIds = userCoupons.stream()
            .map(UserCoupon::getCouponId)
            .collect(Collectors.toList());
        
        List<Coupon> coupons = couponMapper.selectBatchIds(couponIds);
        Map<Long, Coupon> couponMap = new HashMap<>();
        for (Coupon coupon : coupons) {
            couponMap.put(coupon.getId(), coupon);
        }
        
        // 3. 构建VO并判断是否可用
        List<UserCouponVO> result = new ArrayList<>();
        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = couponMap.get(userCoupon.getCouponId());
            if (coupon == null || coupon.getStatus() != 1) {
                continue; // 跳过已下架的优惠券
            }
            
            UserCouponVO vo = UserCouponVO.builder()
                .userCouponId(userCoupon.getId())
                .couponId(coupon.getId())
                .name(coupon.getName())
                .type(coupon.getType())
                .typeName(coupon.getType() == 1 ? "满减券" : "折扣券")
                .discountAmount(coupon.getDiscountAmount())
                .discountRate(coupon.getDiscountRate())
                .minAmount(coupon.getMinAmount())
                .maxDiscount(coupon.getMaxDiscount())
                .expireTime(userCoupon.getExpireTime())
                .description(coupon.getDescription())
                .build();
            
            // 判断是否满足使用条件（查询所有优惠券时，全部标记为可用）
            if (queryAll || coursePrice.compareTo(coupon.getMinAmount()) >= 0) {
                vo.setCanUse(true);
                vo.setUnusableReason(null);
            } else {
                vo.setCanUse(false);
                vo.setUnusableReason("订单金额不足¥" + coupon.getMinAmount());
            }
            
            result.add(vo);
        }
        
        // 4. 排序：可用的在前，不可用的在后
        result.sort((a, b) -> {
            if (a.getCanUse() && !b.getCanUse()) return -1;
            if (!a.getCanUse() && b.getCanUse()) return 1;
            // 按过期时间排序
            return a.getExpireTime().compareTo(b.getExpireTime());
        });
        
        log.info("查询到 {} 张优惠券", result.size());
        
        return Response.success(result);
    }
    
    @Override
    public Response calculateDiscountedPrice(BigDecimal coursePrice, Long userCouponId) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("计算优惠价格，原价：{}，优惠券ID：{}", coursePrice, userCouponId);
        
        // 1. 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            return Response.fail("优惠券不存在");
        }
        
        // 2. 检查优惠券状态
        if (userCoupon.getStatus() != 0) {
            return Response.fail("优惠券已使用");
        }
        
        if (userCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
            return Response.fail("优惠券已过期");
        }
        
        // 3. 查询优惠券详情
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null || coupon.getStatus() != 1) {
            return Response.fail("优惠券已下架");
        }
        
        // 4. 检查是否满足使用条件
        if (coursePrice.compareTo(coupon.getMinAmount()) < 0) {
            return Response.fail("订单金额不足¥" + coupon.getMinAmount());
        }
        
        // 5. 计算优惠金额
        BigDecimal discountAmount;
        if (coupon.getType() == 1) {
            // 满减券：直接减免
            discountAmount = coupon.getDiscountAmount();
        } else {
            // 折扣券：原价 * (1 - 折扣率)，不超过最大优惠金额
            discountAmount = coursePrice.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()))
                .setScale(2, RoundingMode.HALF_UP);
            
            if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                discountAmount = coupon.getMaxDiscount();
            }
        }
        
        // 6. 计算最终价格
        BigDecimal finalPrice = coursePrice.subtract(discountAmount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }
        
        // 7. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("originalPrice", coursePrice);
        result.put("discountAmount", discountAmount);
        result.put("finalPrice", finalPrice);
        result.put("couponName", coupon.getName());
        
        log.info("优惠计算完成：原价={}, 优惠={}, 实付={}", coursePrice, discountAmount, finalPrice);
        
        return Response.success(result);
    }
}

