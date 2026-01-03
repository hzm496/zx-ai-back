package com.zm.zx.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.po.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置Mapper（共享）
 */
@Mapper
public interface AdminSystemConfigMapper extends BaseMapper<SystemConfig> {
}

