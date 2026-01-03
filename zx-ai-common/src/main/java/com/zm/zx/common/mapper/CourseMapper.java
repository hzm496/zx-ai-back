package com.zm.zx.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.course.po.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程表 Mapper 接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}

