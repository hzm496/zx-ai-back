package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日订单趋势 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyOrderTrendVO {
    
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 课程订单数
     */
    private Integer courseOrderCount;
    
    /**
     * VIP订单数
     */
    private Integer vipOrderCount;
    
    /**
     * 总订单数
     */
    private Integer totalOrderCount;
}

