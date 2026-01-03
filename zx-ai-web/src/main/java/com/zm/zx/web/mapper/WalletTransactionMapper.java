package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.domain.po.WalletTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钱包交易记录 Mapper
 */
@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {
}

