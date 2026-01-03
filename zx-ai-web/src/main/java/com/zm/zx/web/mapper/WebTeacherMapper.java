package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.teacher.po.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * Web端 - 讲师Mapper
 */
@Mapper
public interface WebTeacherMapper extends BaseMapper<Teacher> {
}

