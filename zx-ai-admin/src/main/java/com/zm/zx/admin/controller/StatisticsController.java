package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.FindStatisticsListDTO;
import com.zm.zx.admin.service.StatisticsService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 统计数据Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 获取统计列表（分页）
     */
    @ApiOperationLog(description = "获取统计列表")
    @PostMapping("/list")
    public PageResponse list(@RequestBody FindStatisticsListDTO findStatisticsListDTO) {
        return statisticsService.findStatisticsList(findStatisticsListDTO);
    }
    
    /**
     * 查询最近N天的统计数据
     */
    @ApiOperationLog(description = "查询最近N天统计数据")
    @GetMapping("/recent/{days}")
    public Response getRecentDays(@PathVariable("days") Integer days) {
        return statisticsService.getRecentDays(days);
    }
    
    /**
     * 获取仪表盘总览数据
     */
    @ApiOperationLog(description = "获取仪表盘总览数据")
    @GetMapping("/dashboard/overview")
    public Response getDashboardOverview() {
        return statisticsService.getDashboardOverview();
    }
    
    /**
     * 获取课程分类分布
     */
    @ApiOperationLog(description = "获取课程分类分布")
    @GetMapping("/course/distribution")
    public Response getCourseCategoryDistribution() {
        return statisticsService.getCourseCategoryDistribution();
    }
    
    /**
     * 获取最近N天每日注册用户数
     */
    @ApiOperationLog(description = "获取最近N天每日注册用户数")
    @GetMapping("/daily/users/{days}")
    public Response getDailyUserCount(@PathVariable("days") Integer days) {
        return statisticsService.getDailyUserCount(days);
    }
    
    /**
     * 获取最近N天每日订单数趋势
     */
    @ApiOperationLog(description = "获取最近N天每日订单数趋势")
    @GetMapping("/daily/orders/{days}")
    public Response getDailyOrderTrend(@PathVariable("days") Integer days) {
        return statisticsService.getDailyOrderTrend(days);
    }
    
    /**
     * 获取最近N天每日新增课程数
     */
    @ApiOperationLog(description = "获取最近N天每日新增课程数")
    @GetMapping("/daily/courses/{days}")
    public Response getDailyCourseCount(@PathVariable("days") Integer days) {
        return statisticsService.getDailyCourseCount(days);
    }
}

