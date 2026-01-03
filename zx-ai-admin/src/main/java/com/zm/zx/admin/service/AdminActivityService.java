package com.zm.zx.admin.service;

import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.admin.model.dto.ActivityAddDTO;
import com.zm.zx.admin.model.dto.ActivityUpdateDTO;
import com.zm.zx.admin.model.dto.FindActivityListDTO;

/**
 * 活动服务接口（管理端）
 */
public interface AdminActivityService {

    /**
     * 分页查询活动列表
     */
    PageResponse findActivityList(FindActivityListDTO dto);

    /**
     * 添加活动
     */
    Response addActivity(ActivityAddDTO dto);

    /**
     * 更新活动
     */
    Response updateActivity(ActivityUpdateDTO dto);

    /**
     * 删除活动
     */
    Response deleteActivity(Long id);

    /**
     * 启用/禁用活动
     */
    Response updateActivityStatus(Long id, Integer status);
}
