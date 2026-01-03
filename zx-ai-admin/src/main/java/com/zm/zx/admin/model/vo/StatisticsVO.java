package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 统计数据 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsVO {
    
    /**
     * 统计ID
     */
    private Long id;
    
    /**
     * 统计日期
     */
    private LocalDate statDate;
    
    /**
     * 新增用户数
     */
    private Integer newUserCount;
    
    /**
     * 活跃用户数
     */
    private Integer activeUserCount;
    
    /**
     * 新增订单数
     */
    private Integer newOrderCount;
    
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 新增课程数
     */
    private Integer newCourseCount;
    
    /**
     * 学习时长（秒）
     */
    private Long learningDuration;
}

