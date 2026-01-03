package com.zm.zx.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.course.po.CourseChapter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程章节表 Mapper 接口
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {
}

