package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日用户统计 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyUserCountVO {
    
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 注册用户数
     */
    private Integer userCount;
}

