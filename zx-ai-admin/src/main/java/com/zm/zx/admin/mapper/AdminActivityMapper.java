package com.zm.zx.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.admin.model.dto.FindActivityListDTO;
import com.zm.zx.common.model.activity.po.Activity;
import com.zm.zx.admin.model.vo.ActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动 Mapper（管理端）
 */
@Mapper
public interface AdminActivityMapper extends BaseMapper<Activity> {

    /**
     * 分页查询活动列表
     */
    IPage<ActivityVO> findActivityList(Page<ActivityVO> page, @Param("dto") FindActivityListDTO dto);
}
