package com.zm.zx.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zm.zx.admin.model.dto.FindStatisticsListDTO;
import com.zm.zx.admin.model.po.Statistics;
import com.zm.zx.admin.model.vo.CourseCategoryDistributionVO;
import com.zm.zx.admin.model.vo.DailyCourseCountVO;
import com.zm.zx.admin.model.vo.DailyOrderTrendVO;
import com.zm.zx.admin.model.vo.DailyUserCountVO;
import com.zm.zx.admin.model.vo.DashboardOverviewVO;
import com.zm.zx.admin.model.vo.StatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计数据Mapper接口
 */
@Mapper
public interface StatisticsMapper extends BaseMapper<Statistics> {
    
    /**
     * 分页查询统计列表
     */
    IPage<StatisticsVO> findStatisticsList(IPage<StatisticsVO> page, @Param("dto") FindStatisticsListDTO findStatisticsListDTO);
    
    /**
     * 查询最近N天的统计数据
     */
    List<StatisticsVO> findRecentDays(@Param("days") Integer days);
    
    /**
     * 获取仪表盘总览数据
     */
    DashboardOverviewVO getDashboardOverview();
    
    /**
     * 获取课程分类分布
     */
    List<CourseCategoryDistributionVO> getCourseCategoryDistribution();
    
    /**
     * 获取最近N天每日注册用户数
     */
    List<DailyUserCountVO> getDailyUserCount(@Param("days") Integer days);
    
    /**
     * 获取最近N天每日订单数趋势（分课程订单和VIP订单）
     */
    List<DailyOrderTrendVO> getDailyOrderTrend(@Param("days") Integer days);
    
    /**
     * 获取最近N天每日新增课程数
     */
    List<DailyCourseCountVO> getDailyCourseCount(@Param("days") Integer days);
}

