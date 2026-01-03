package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.ActivityAddDTO;
import com.zm.zx.admin.model.dto.ActivityUpdateDTO;
import com.zm.zx.admin.model.dto.FindActivityListDTO;
import com.zm.zx.admin.service.AdminActivityService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 活动管理 Controller（管理端）
 */
@SaCheckRole("admin")
@Slf4j
@RestController
@RequestMapping("/admin/activity")
@RequiredArgsConstructor
public class AdminActivityController {

    private final AdminActivityService activityService;

    /**
     * 分页查询活动列表
     */
    @ApiOperationLog(description = "查询活动列表")
    @PostMapping("/list")
    public PageResponse getActivityList(@RequestBody FindActivityListDTO dto) {
        return activityService.findActivityList(dto);
    }

    /**
     * 添加活动
     */
    @ApiOperationLog(description = "添加活动")
    @PostMapping("/add")
    public Response addActivity(@Validated @RequestBody ActivityAddDTO dto) {
        return activityService.addActivity(dto);
    }

    /**
     * 更新活动
     */
    @ApiOperationLog(description = "更新活动")
    @PostMapping("/update")
    public Response updateActivity(@Validated @RequestBody ActivityUpdateDTO dto) {
        return activityService.updateActivity(dto);
    }

    /**
     * 删除活动
     */
    @ApiOperationLog(description = "删除活动")
    @DeleteMapping("/delete/{id}")
    public Response deleteActivity(@PathVariable("id") Long id) {
        return activityService.deleteActivity(id);
    }

    /**
     * 启用/禁用活动
     */
    @ApiOperationLog(description = "更新活动状态")
    @PutMapping("/status/{id}")
    public Response updateActivityStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        return activityService.updateActivityStatus(id, status);
    }
}

