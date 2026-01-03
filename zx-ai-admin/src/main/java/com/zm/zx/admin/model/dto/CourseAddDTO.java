package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 添加课程 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseAddDTO {
    
    @NotBlank(message = "课程标题不能为空")
    private String title;
    
    private String subTitle;
    
    private String cover;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    private Long teacherId;
    
    private String description;
    
    private String outline;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer isFree;
    
    private Integer difficulty;
    
    private Integer duration;
    
    private Integer status;
    
    private Integer isTrial;
    
    private Integer trialChapterCount;
    
    private Integer sort;
}

