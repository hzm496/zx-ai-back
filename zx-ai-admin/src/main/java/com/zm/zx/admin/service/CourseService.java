package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.CourseAddDTO;
import com.zm.zx.admin.model.dto.CourseUpdateDTO;
import com.zm.zx.admin.model.dto.FindCourseListDTO;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * 课程Service接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 获取课程列表（分页）
     */
    PageResponse findCourseList(FindCourseListDTO findCourseListDTO);
    
    /**
     * 添加课程
     */
    Response addCourse(CourseAddDTO courseAddDTO);
    
    /**
     * 更新课程信息
     */
    Response updateCourse(CourseUpdateDTO courseUpdateDTO);
    
    /**
     * 删除课程
     */
    Response deleteCourse(Long id);
    
    /**
     * 根据ID获取课程详情
     */
    Response getCourseById(Long id);
}

