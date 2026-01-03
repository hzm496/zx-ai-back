package com.zm.zx.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.mapper.CourseCategoryMapper;
import com.zm.zx.common.mapper.CourseMapper;
import com.zm.zx.common.model.course.po.CourseCategory;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.web.domain.vo.CategoryVO;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.WebCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Web - 课程分类Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WebCategoryServiceImpl implements WebCategoryService {
    
    private final CourseCategoryMapper categoryMapper;
    private final CourseMapper courseMapper;
    
    @Override
    public Response findAllCategories() {
        LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseCategory::getStatus, 1); // 只返回正常状态的分类
        lqw.orderByAsc(CourseCategory::getSort);
        lqw.orderByDesc(CourseCategory::getCreateTime);
        
        List<CourseCategory> categories = categoryMapper.selectList(lqw);
        List<CategoryVO> categoryVOS = categories.stream()
                .map(category -> BeanUtil.copyProperties(category, CategoryVO.class))
                .collect(Collectors.toList());
        
        return Response.success(categoryVOS);
    }
    
    @Override
    public Response getTopCategories() {
        LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseCategory::getParentId, 0);
        lqw.eq(CourseCategory::getStatus, 1); // 只返回正常状态的分类
        lqw.orderByAsc(CourseCategory::getSort);
        
        List<CourseCategory> categories = categoryMapper.selectList(lqw);
        List<CategoryVO> categoryVOS = categories.stream()
                .map(category -> BeanUtil.copyProperties(category, CategoryVO.class))
                .collect(Collectors.toList());
        
        return Response.success(categoryVOS);
    }
    
    @Override
    public Response getCategoryById(Long id) {
        CourseCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }
        
        // Web端只返回正常状态的分类
        if (category.getStatus() != 1) {
            throw new BizException("分类已禁用");
        }
        
        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        return Response.success(categoryVO);
    }
    
    @Override
    public Response getHotCategories() {
        log.info("获取热门分类列表");
        
        // 查询所有正常状态的顶级分类
        LambdaQueryWrapper<CourseCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseCategory::getParentId, 0);
        lqw.eq(CourseCategory::getStatus, 1);
        lqw.orderByAsc(CourseCategory::getSort);
        
        List<CourseCategory> categories = categoryMapper.selectList(lqw);
        
        // 为每个分类统计课程数量
        List<CategoryVO> categoryVOS = categories.stream()
                .map(category -> {
                    CategoryVO vo = BeanUtil.copyProperties(category, CategoryVO.class);
                    
                    // 统计该分类下的课程数量（只统计上架状态的课程）
                    LambdaQueryWrapper<Course> courseQuery = new LambdaQueryWrapper<>();
                    courseQuery.eq(Course::getCategoryId, category.getId())
                               .eq(Course::getStatus, 1);
                    Long courseCount = courseMapper.selectCount(courseQuery);
                    vo.setCourseCount(courseCount.intValue());
                    
                    return vo;
                })
                .sorted((a, b) -> b.getCourseCount().compareTo(a.getCourseCount())) // 按课程数量降序
                .limit(8) // 只返回前8个
                .collect(Collectors.toList());
        
        log.info("热门分类查询成功，共{}个分类", categoryVOS.size());
        return Response.success(categoryVOS);
    }
}

