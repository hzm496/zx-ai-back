package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.mapper.CourseCategoryMapper;
import com.zm.zx.admin.model.dto.CategoryAddDTO;
import com.zm.zx.admin.model.dto.CategoryUpdateDTO;
import com.zm.zx.common.model.course.po.CourseCategory;
import com.zm.zx.admin.model.vo.CategoryVO;
import com.zm.zx.admin.service.AdminCourseCategoryService;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin - 课程分类Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminCourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> 
        implements AdminCourseCategoryService {
    
    @Override
    public Response findAllCategories() {
        LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(CourseCategory::getSort);
        lqw.orderByDesc(CourseCategory::getCreateTime);
        
        List<CourseCategory> categories = this.list(lqw);
        List<CategoryVO> categoryVOS = categories.stream()
                .map(category -> BeanUtil.copyProperties(category, CategoryVO.class))
                .collect(Collectors.toList());
        
        return Response.success(categoryVOS);
    }
    
    @Override
    public Response addCategory(CategoryAddDTO categoryAddDTO) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseCategory::getName, categoryAddDTO.getName());
        CourseCategory existCategory = this.getOne(lqw);
        if (existCategory != null) {
            throw new BizException("分类名称已存在");
        }
        
        // 如果指定了父分类，检查父分类是否存在
        if (categoryAddDTO.getParentId() != null && categoryAddDTO.getParentId() != 0) {
            CourseCategory parentCategory = this.getById(categoryAddDTO.getParentId());
            if (parentCategory == null) {
                throw new BizException("父分类不存在");
            }
        }
        
        // 添加分类
        CourseCategory category = BeanUtil.copyProperties(categoryAddDTO, CourseCategory.class);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        boolean saved = this.save(category);
        if (!saved) {
            throw new BizException(ResponseEnum.ADD_FAIL);
        }
        
        return Response.success();
    }
    
    @Override
    public Response updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        // 检查分类是否存在
        CourseCategory category = this.getById(categoryUpdateDTO.getId());
        if (category == null) {
            throw new BizException("分类不存在");
        }
        
        // 如果修改了名称，检查新名称是否已被其他分类使用
        if (categoryUpdateDTO.getName() != null 
                && !categoryUpdateDTO.getName().equals(category.getName())) {
            LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
            lqw.eq(CourseCategory::getName, categoryUpdateDTO.getName());
            lqw.ne(CourseCategory::getId, categoryUpdateDTO.getId());
            CourseCategory existCategory = this.getOne(lqw);
            if (existCategory != null) {
                throw new BizException("分类名称已存在");
            }
        }
        
        // 如果修改了父分类，检查新父分类是否存在
        if (categoryUpdateDTO.getParentId() != null && categoryUpdateDTO.getParentId() != 0) {
            CourseCategory parentCategory = this.getById(categoryUpdateDTO.getParentId());
            if (parentCategory == null) {
                throw new BizException("父分类不存在");
            }
            // 不能将自己设为自己的父分类
            if (categoryUpdateDTO.getParentId().equals(categoryUpdateDTO.getId())) {
                throw new BizException("不能将自己设为父分类");
            }
        }
        
        // 更新分类信息
        CourseCategory updateCategory = BeanUtil.copyProperties(categoryUpdateDTO, CourseCategory.class);
        boolean updated = this.updateById(updateCategory);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }
        
        return Response.success();
    }
    
    @Override
    public Response deleteCategory(Long id) {
        // 检查是否有子分类
        LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseCategory::getParentId, id);
        long count = this.count(lqw);
        if (count > 0) {
            throw new BizException("该分类下有子分类，无法删除");
        }
        
        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }
        return Response.success();
    }
    
    @Override
    public Response getCategoryById(Long id) {
        CourseCategory category = this.getById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }
        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        return Response.success(categoryVO);
    }
}

