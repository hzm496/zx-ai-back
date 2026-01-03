package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.domain.po.UserWallet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户钱包 Mapper
 */
@Mapper
public interface UserWalletMapper extends BaseMapper<UserWallet> {
}

