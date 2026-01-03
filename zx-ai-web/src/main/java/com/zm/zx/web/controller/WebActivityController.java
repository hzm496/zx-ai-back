package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.WebActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 活动 Controller（用户端）
 */
@Slf4j
@RestController
@RequestMapping("/web/activity")
@RequiredArgsConstructor
public class WebActivityController {

    private final WebActivityService activityService;

    /**
     * 查询可领取的活动列表
     */
    @ApiOperationLog(description = "查询活动列表")
    @GetMapping("/list")
    public Response getAvailableActivities() {
        return activityService.getAvailableActivities();
    }

    /**
     * 领取活动奖励（需要登录）
     */
    @SaCheckLogin
    @ApiOperationLog(description = "领取活动")
    @PostMapping("/receive/{activityId}")
    public Response receiveActivity(@PathVariable("activityId") Long activityId) {
        return activityService.receiveActivity(activityId);
    }
}

