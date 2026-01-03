package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.CategoryAddDTO;
import com.zm.zx.admin.model.dto.CategoryUpdateDTO;
import com.zm.zx.common.model.course.po.CourseCategory;
import com.zm.zx.common.response.Response;

/**
 * Admin - 课程分类Service接口
 */
public interface AdminCourseCategoryService extends IService<CourseCategory> {
    
    /**
     * 获取所有分类列表（树形结构）
     */
    Response findAllCategories();
    
    /**
     * 添加分类
     */
    Response addCategory(CategoryAddDTO categoryAddDTO);
    
    /**
     * 更新分类信息
     */
    Response updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    
    /**
     * 删除分类
     */
    Response deleteCategory(Long id);
    
    /**
     * 根据ID获取分类详情
     */
    Response getCategoryById(Long id);
}

