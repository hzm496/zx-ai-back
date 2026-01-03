package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 仪表盘总览数据 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardOverviewVO {
    
    /**
     * 总用户数
     */
    private Long totalUsers;
    
    /**
     * 总课程数
     */
    private Long totalCourses;
    
    /**
     * 总订单数
     */
    private Long totalOrders;
    
    /**
     * 总收入
     */
    private BigDecimal totalRevenue;
    
    /**
     * 今日新增用户
     */
    private Integer todayNewUsers;
    
    /**
     * 今日新增订单
     */
    private Integer todayNewOrders;
    
    /**
     * 今日收入
     */
    private BigDecimal todayRevenue;
    
    /**
     * 本月新增用户
     */
    private Integer monthNewUsers;
    
    /**
     * 本月新增订单
     */
    private Integer monthNewOrders;
    
    /**
     * 本月收入
     */
    private BigDecimal monthRevenue;
}

