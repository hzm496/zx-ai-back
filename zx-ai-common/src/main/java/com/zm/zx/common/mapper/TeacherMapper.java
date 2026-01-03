package com.zm.zx.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.teacher.po.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讲师表 Mapper 接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}

