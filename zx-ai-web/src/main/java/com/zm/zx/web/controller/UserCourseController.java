package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户课程 Controller
 */
@Slf4j
@RestController
@RequestMapping("/web/user/course")
@RequiredArgsConstructor
public class UserCourseController {

    private final UserCourseService userCourseService;

    /**
     * 分页查询用户的课程列表（我的课程）
     */
    @ApiOperationLog(description = "分页查询我的课程")
    @GetMapping("/my")
    public PageResponse getUserCoursesPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userCourseService.getUserCoursesPage(pageNo, pageSize);
    }

    /**
     * 检查用户是否拥有课程
     */
    @ApiOperationLog(description = "检查是否拥有课程")
    @GetMapping("/check/{courseId}")
    public Response checkUserHasCourse(@PathVariable("courseId") Long courseId) {
        return userCourseService.checkUserHasCourse(courseId);
    }
}

