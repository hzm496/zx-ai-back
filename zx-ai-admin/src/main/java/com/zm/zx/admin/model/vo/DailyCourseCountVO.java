package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日课程统计 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyCourseCountVO {
    
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 新增课程数
     */
    private Integer courseCount;
}

