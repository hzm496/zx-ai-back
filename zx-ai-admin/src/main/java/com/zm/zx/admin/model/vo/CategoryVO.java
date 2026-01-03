package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分类 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryVO {
    
    private Long id;
    private String name;
    private Long parentId;
    private String icon;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

