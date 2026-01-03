package com.zm.zx.web.service;

import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * 用户课程服务
 */
public interface UserCourseService {

    /**
     * 分页查询用户的课程列表
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @return 课程列表
     */
    PageResponse getUserCoursesPage(Integer pageNo, Integer pageSize);

    /**
     * 检查用户是否拥有课程
     *
     * @param courseId 课程ID
     * @return 是否拥有
     */
    Response checkUserHasCourse(Long courseId);
}

