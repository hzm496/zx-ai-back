package com.zm.zx.admin.model.dto;

import com.zm.zx.common.model.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询课程列表 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCourseListDTO extends BaseQuery {
    
    /**
     * 课程标题（模糊查询）
     */
    private String title;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 状态：0-下架，1-上架，2-审核中
     */
    private Integer status;
    
    /**
     * 是否免费：0-付费，1-免费
     */
    private Integer isFree;
}

