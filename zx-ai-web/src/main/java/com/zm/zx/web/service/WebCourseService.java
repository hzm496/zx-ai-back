package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

/**
 * Web端 - 课程Service接口
 */
public interface WebCourseService {
    
    /**
     * 根据讲师ID获取课程列表（只返回上架状态）
     */
    Response getCoursesByTeacherId(Long teacherId);
    
    /**
     * 获取推荐课程列表（首页精品推荐，最多6门）
     */
    Response getRecommendCourses();
    
    /**
     * 根据分类ID分页查询课程列表（只返回上架状态）
     * @param categoryId 分类ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param isFree 是否免费（null-全部，1-免费，0-付费）
     */
    Response getCoursesByCategory(Long categoryId, Integer pageNo, Integer pageSize, Integer isFree);
    
    /**
     * 获取课程详情（包含章节信息）
     * @param courseId 课程ID
     */
    Response getCourseDetail(Long courseId);
    
    /**
     * 获取课程章节列表（树形结构）
     * @param courseId 课程ID
     */
    Response getCourseChapters(Long courseId);
    
    /**
     * 获取所有课程分类（包含每个分类的课程数量）
     */
    Response getCourseCategories();
    
    /**
     * 根据标题搜索课程（模糊匹配）
     * @param title 课程标题关键词
     */
    Response searchCourseByTitle(String title);
}

