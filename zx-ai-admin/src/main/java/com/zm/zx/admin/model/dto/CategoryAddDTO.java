package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加分类 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAddDTO {
    
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    private Long parentId;
    
    private String icon;
    
    private Integer sort;
    
    private Integer status;
}

