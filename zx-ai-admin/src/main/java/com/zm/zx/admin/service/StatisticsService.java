package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.FindStatisticsListDTO;
import com.zm.zx.admin.model.po.Statistics;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * 统计数据Service接口
 */
public interface StatisticsService extends IService<Statistics> {
    
    /**
     * 获取统计列表（分页）
     */
    PageResponse findStatisticsList(FindStatisticsListDTO findStatisticsListDTO);
    
    /**
     * 查询最近N天的统计数据
     */
    Response getRecentDays(Integer days);
    
    /**
     * 获取仪表盘总览数据
     */
    Response getDashboardOverview();
    
    /**
     * 获取课程分类分布
     */
    Response getCourseCategoryDistribution();
    
    /**
     * 获取最近N天每日注册用户数
     */
    Response getDailyUserCount(Integer days);
    
    /**
     * 获取最近N天每日订单数趋势
     */
    Response getDailyOrderTrend(Integer days);
    
    /**
     * 获取最近N天每日新增课程数
     */
    Response getDailyCourseCount(Integer days);
}

