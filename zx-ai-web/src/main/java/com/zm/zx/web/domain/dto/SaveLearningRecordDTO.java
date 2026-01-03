package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 保存学习记录 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveLearningRecordDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 章节ID
     */
    @NotNull(message = "章节ID不能为空")
    private Long chapterId;
    
    /**
     * 观看进度（秒）
     */
    @NotNull(message = "观看进度不能为空")
    @Min(value = 0, message = "观看进度不能为负数")
    private Integer progress;
    
    /**
     * 章节总时长（秒）
     */
    @NotNull(message = "章节总时长不能为空")
    @Min(value = 0, message = "章节总时长不能为负数")
    private Integer duration;
}

