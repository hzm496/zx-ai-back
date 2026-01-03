package com.zm.zx.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询反馈列表 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindFeedbackListDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 是否已读（null-全部，0-未读，1-已读）
     */
    private Integer isRead;
    
    /**
     * 搜索关键词（用户名、邮箱、内容）
     */
    private String keyword;
}

