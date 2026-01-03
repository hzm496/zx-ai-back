package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.model.po.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券 Mapper
 */
@Mapper
public interface WebCouponMapper extends BaseMapper<Coupon> {
}

