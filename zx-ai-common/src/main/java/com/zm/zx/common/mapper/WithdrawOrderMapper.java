package com.zm.zx.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.withdraw.po.WithdrawOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现订单表 Mapper 接口
 */
@Mapper
public interface WithdrawOrderMapper extends BaseMapper<WithdrawOrder> {
}

