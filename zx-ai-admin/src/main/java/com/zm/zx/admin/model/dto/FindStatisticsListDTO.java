package com.zm.zx.admin.model.dto;

import com.zm.zx.common.model.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 查询统计列表 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindStatisticsListDTO extends BaseQuery {
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
}

