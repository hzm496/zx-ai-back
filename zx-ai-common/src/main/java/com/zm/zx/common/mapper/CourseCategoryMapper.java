package com.zm.zx.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.course.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类表 Mapper 接口
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
}

