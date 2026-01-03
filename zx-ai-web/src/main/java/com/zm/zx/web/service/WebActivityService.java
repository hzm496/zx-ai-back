package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

/**
 * 活动服务接口（用户端）
 */
public interface WebActivityService {

    /**
     * 查询可领取的活动列表
     */
    Response getAvailableActivities();

    /**
     * 领取活动奖励
     */
    Response receiveActivity(Long activityId);
}

