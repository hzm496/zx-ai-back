package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新章节 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterUpdateDTO {
    
    @NotNull(message = "章节ID不能为空")
    private Long id;
    
    private String title;
    
    private String videoUrl;
    
    private Integer videoDuration;
    
    private Long parentId;
    
    private Integer sort;
    
    private Integer isFree;
    
    private Integer status;
}

