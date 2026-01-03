package com.zm.zx.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.admin.mapper.StatisticsMapper;
import com.zm.zx.admin.model.dto.FindStatisticsListDTO;
import com.zm.zx.admin.model.po.Statistics;
import com.zm.zx.admin.model.vo.DashboardOverviewVO;
import com.zm.zx.admin.model.vo.StatisticsVO;
import com.zm.zx.admin.service.StatisticsService;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 统计数据Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements StatisticsService {
    
    private final StatisticsMapper statisticsMapper;
    
    @Override
    public PageResponse findStatisticsList(FindStatisticsListDTO findStatisticsListDTO) {
        Integer pageSize = findStatisticsListDTO.getPageSize();
        Integer pageNum = findStatisticsListDTO.getPageNum();
        
        Page<StatisticsVO> page = new Page<>(pageNum, pageSize);
        IPage<StatisticsVO> statisticsPage = statisticsMapper.findStatisticsList(page, findStatisticsListDTO);
        
        List<StatisticsVO> records = statisticsPage.getRecords();
        
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }
        
        return PageResponse.success(records, pageNum, statisticsPage.getTotal(), pageSize);
    }
    
    @Override
    public Response getRecentDays(Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认查询最近7天
        }
        if (days > 365) {
            days = 365; // 最多查询一年
        }
        
        List<StatisticsVO> statistics = statisticsMapper.findRecentDays(days);
        return Response.success(statistics);
    }
    
    @Override
    public Response getDashboardOverview() {
        DashboardOverviewVO overview = statisticsMapper.getDashboardOverview();
        return Response.success(overview);
    }
    
    @Override
    public Response getCourseCategoryDistribution() {
        return Response.success(statisticsMapper.getCourseCategoryDistribution());
    }
    
    @Override
    public Response getDailyUserCount(Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认查询最近7天
        }
        if (days > 365) {
            days = 365; // 最多查询一年
        }
        return Response.success(statisticsMapper.getDailyUserCount(days));
    }
    
    @Override
    public Response getDailyOrderTrend(Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认查询最近7天
        }
        if (days > 30) {
            days = 30; // 最多查询30天
        }
        return Response.success(statisticsMapper.getDailyOrderTrend(days));
    }
    
    @Override
    public Response getDailyCourseCount(Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认查询最近7天
        }
        if (days > 365) {
            days = 365; // 最多查询一年
        }
        return Response.success(statisticsMapper.getDailyCourseCount(days));
    }
}

