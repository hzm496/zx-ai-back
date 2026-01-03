package com.zm.zx.web.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 课程分类VO
 */
@Data
public class CourseCategoryVO {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 父级分类ID，0表示顶级分类
     */
    private Long parentId;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 该分类下的课程数量
     */
    private Integer courseCount;
    
    /**
     * 子分类列表
     */
    private List<CourseCategoryVO> children;
}

