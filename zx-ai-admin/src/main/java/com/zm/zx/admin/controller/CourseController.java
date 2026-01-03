package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.CourseAddDTO;
import com.zm.zx.admin.model.dto.CourseUpdateDTO;
import com.zm.zx.admin.model.dto.FindCourseListDTO;
import com.zm.zx.admin.service.CourseService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/course")
public class CourseController {
    
    private final CourseService courseService;
    
    /**
     * 获取课程列表（分页）
     */
    @ApiOperationLog(description = "获取课程列表")
    @PostMapping("/list")
    public PageResponse list(@RequestBody FindCourseListDTO findCourseListDTO) {
        return courseService.findCourseList(findCourseListDTO);
    }
    
    /**
     * 添加课程
     */
    @ApiOperationLog(description = "添加课程")
    @PostMapping("/add")
    public Response add(@Validated @RequestBody CourseAddDTO courseAddDTO) {
        return courseService.addCourse(courseAddDTO);
    }
    
    /**
     * 更新课程信息
     */
    @ApiOperationLog(description = "更新课程信息")
    @PutMapping("/update")
    public Response update(@Validated @RequestBody CourseUpdateDTO courseUpdateDTO) {
        return courseService.updateCourse(courseUpdateDTO);
    }
    
    /**
     * 删除课程
     */
    @ApiOperationLog(description = "删除课程")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long id) {
        return courseService.deleteCourse(id);
    }
    
    /**
     * 根据ID获取课程详情
     */
    @ApiOperationLog(description = "获取课程详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return courseService.getCourseById(id);
    }
}

