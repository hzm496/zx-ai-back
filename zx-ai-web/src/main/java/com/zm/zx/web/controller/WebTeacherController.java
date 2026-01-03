package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.WebTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Web端 - 讲师Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/teacher")
public class WebTeacherController {
    
    private final WebTeacherService teacherService;
    
    /**
     * 获取所有讲师列表（只返回正常状态）
     */
    @ApiOperationLog(description = "获取讲师列表")
    @GetMapping("/list")
    public Response list() {
        return teacherService.findAllTeachers();
    }
    
    /**
     * 分页查询讲师列表
     */
    @ApiOperationLog(description = "分页查询讲师列表")
    @GetMapping("/page")
    public Response page(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize
    ) {
        return teacherService.getTeacherListByPage(pageNo, pageSize);
    }
}

