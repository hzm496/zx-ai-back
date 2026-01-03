package com.zm.zx.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.po.Membership;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员套餐表 Mapper 接口
 */
@Mapper
public interface MembershipMapper extends BaseMapper<Membership> {
}

