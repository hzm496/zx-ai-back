package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程分类分布 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCategoryDistributionVO {
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 课程数量
     */
    private Long courseCount;
}

