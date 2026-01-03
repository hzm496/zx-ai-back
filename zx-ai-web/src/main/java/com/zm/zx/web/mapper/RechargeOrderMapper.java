package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.domain.po.RechargeOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充值订单Mapper
 */
@Mapper
public interface RechargeOrderMapper extends BaseMapper<RechargeOrder> {
}

