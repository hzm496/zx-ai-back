package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加章节 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterAddDTO {
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotBlank(message = "章节标题不能为空")
    private String title;
    
    private String videoUrl;
    
    private Integer videoDuration;
    
    private Long parentId;
    
    private Integer sort;
    
    private Integer isFree;
    
    private Integer status;
}

