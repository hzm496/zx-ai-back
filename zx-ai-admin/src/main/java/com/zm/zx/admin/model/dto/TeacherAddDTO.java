package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加讲师 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherAddDTO {
    
    /**
     * 讲师姓名
     */
    @NotBlank(message = "讲师姓名不能为空")
    private String name;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 简介
     */
    private String intro;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 工作经历
     */
    private String experience;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sort;
}

