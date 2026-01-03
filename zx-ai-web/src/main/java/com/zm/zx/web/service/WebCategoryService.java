package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

/**
 * Web - 课程分类Service接口
 */
public interface WebCategoryService {
    
    /**
     * 获取所有分类列表（只返回正常状态）
     */
    Response findAllCategories();
    
    /**
     * 获取顶级分类列表（parentId = 0，只返回正常状态）
     */
    Response getTopCategories();
    
    /**
     * 根据ID获取分类详情（只返回正常状态）
     */
    Response getCategoryById(Long id);
    
    /**
     * 获取热门分类列表（包含课程数量，按课程数量排序，最多返回8个）
     */
    Response getHotCategories();
}

