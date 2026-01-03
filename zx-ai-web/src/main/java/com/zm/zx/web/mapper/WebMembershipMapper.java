package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.po.Membership;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员配置 Mapper (Web前台)
 */
@Mapper
public interface WebMembershipMapper extends BaseMapper<Membership> {
}

