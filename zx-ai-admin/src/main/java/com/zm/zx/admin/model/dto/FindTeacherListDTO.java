package com.zm.zx.admin.model.dto;

import com.zm.zx.common.model.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询讲师列表 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTeacherListDTO extends BaseQuery {
    
    /**
     * 讲师姓名（模糊查询）
     */
    private String name;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
}

