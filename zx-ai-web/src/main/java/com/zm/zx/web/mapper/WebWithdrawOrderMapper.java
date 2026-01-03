package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.withdraw.po.WithdrawOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现订单Mapper（前台用户端）
 */
@Mapper
public interface WebWithdrawOrderMapper extends BaseMapper<WithdrawOrder> {
}

