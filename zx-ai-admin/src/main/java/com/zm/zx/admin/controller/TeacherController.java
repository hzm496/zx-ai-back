package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.FindTeacherListDTO;
import com.zm.zx.admin.model.dto.TeacherAddDTO;
import com.zm.zx.admin.model.dto.TeacherUpdateDTO;
import com.zm.zx.admin.service.TeacherService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 讲师管理Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/teacher")
public class TeacherController {
    
    private final TeacherService teacherService;
    
    /**
     * 获取讲师列表（分页）
     */
    @ApiOperationLog(description = "获取讲师列表")
    @PostMapping("/list")
    public PageResponse list(@RequestBody FindTeacherListDTO findTeacherListDTO) {
        return teacherService.findTeacherList(findTeacherListDTO);
    }
    
    /**
     * 添加讲师
     */
    @ApiOperationLog(description = "添加讲师")
    @PostMapping("/add")
    public Response add(@Validated @RequestBody TeacherAddDTO teacherAddDTO) {
        return teacherService.addTeacher(teacherAddDTO);
    }
    
    /**
     * 更新讲师信息
     */
    @ApiOperationLog(description = "更新讲师信息")
    @PutMapping("/update")
    public Response update(@Validated @RequestBody TeacherUpdateDTO teacherUpdateDTO) {
        return teacherService.updateTeacher(teacherUpdateDTO);
    }
    
    /**
     * 删除讲师
     */
    @ApiOperationLog(description = "删除讲师")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long id) {
        return teacherService.deleteTeacher(id);
    }
    
    /**
     * 根据ID获取讲师详情
     */
    @ApiOperationLog(description = "获取讲师详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return teacherService.getTeacherById(id);
    }
}

