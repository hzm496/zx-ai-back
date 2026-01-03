package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.WebCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Web端 - 课程Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/course")
public class WebCourseController {
    
    private final WebCourseService courseService;
    
    /**
     * 根据讲师ID获取课程列表
     */
    @ApiOperationLog(description = "根据讲师ID获取课程列表")
    @GetMapping("/teacher/{teacherId}")
    public Response getCoursesByTeacherId(@PathVariable("teacherId") Long teacherId) {
        return courseService.getCoursesByTeacherId(teacherId);
    }
    
    /**
     * 获取推荐课程列表（首页精品推荐）
     */
    @ApiOperationLog(description = "获取推荐课程列表")
    @GetMapping("/recommend")
    public Response getRecommendCourses() {
        return courseService.getRecommendCourses();
    }
    
    /**
     * 根据分类ID分页查询课程列表
     */
    @ApiOperationLog(description = "根据分类ID分页查询课程列表")
    @GetMapping("/category/{categoryId}")
    public Response getCoursesByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "12") Integer pageSize,
            @RequestParam(value = "isFree", required = false) Integer isFree
    ) {
        return courseService.getCoursesByCategory(categoryId, pageNo, pageSize, isFree);
    }
    
    /**
     * 获取课程详情（包含章节信息）
     */
    @ApiOperationLog(description = "获取课程详情")
    @GetMapping("/detail/{courseId}")
    public Response getCourseDetail(@PathVariable("courseId") Long courseId) {
        return courseService.getCourseDetail(courseId);
    }
    
    /**
     * 获取课程章节列表（树形结构）
     */
    @ApiOperationLog(description = "获取课程章节列表")
    @GetMapping("/chapters/{courseId}")
    public Response getCourseChapters(@PathVariable("courseId") Long courseId) {
        return courseService.getCourseChapters(courseId);
    }
    
    /**
     * 获取所有课程分类
     */
    @ApiOperationLog(description = "获取所有课程分类")
    @GetMapping("/categories")
    public Response getCourseCategories() {
        return courseService.getCourseCategories();
    }
    
    /**
     * 根据标题搜索课程（用于AI助手课程推荐跳转）
     */
    @ApiOperationLog(description = "搜索课程")
    @GetMapping("/search")
    public Response searchCourseByTitle(@RequestParam("title") String title) {
        return courseService.searchCourseByTitle(title);
    }
}

