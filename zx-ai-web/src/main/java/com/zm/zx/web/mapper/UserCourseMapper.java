package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.web.model.po.UserCourse;
import com.zm.zx.web.model.vo.MyCourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户课程关联 Mapper
 */
@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {

    /**
     * 分页查询用户的课程列表
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 课程列表
     */
    IPage<MyCourseVO> findUserCoursesPage(Page<MyCourseVO> page, @Param("userId") Long userId);

    /**
     * 检查用户是否拥有课程
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 是否拥有
     */
    boolean checkUserHasCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}

