package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新分类 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryUpdateDTO {
    
    @NotNull(message = "分类ID不能为空")
    private Long id;
    
    private String name;
    
    private Long parentId;
    
    private String icon;
    
    private Integer sort;
    
    private Integer status;
}

