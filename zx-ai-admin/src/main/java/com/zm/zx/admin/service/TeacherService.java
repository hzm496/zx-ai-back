package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.FindTeacherListDTO;
import com.zm.zx.admin.model.dto.TeacherAddDTO;
import com.zm.zx.admin.model.dto.TeacherUpdateDTO;
import com.zm.zx.common.model.teacher.po.Teacher;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * 讲师Service接口
 */
public interface TeacherService extends IService<Teacher> {
    
    /**
     * 获取讲师列表（分页）
     */
    PageResponse findTeacherList(FindTeacherListDTO findTeacherListDTO);
    
    /**
     * 添加讲师
     */
    Response addTeacher(TeacherAddDTO teacherAddDTO);
    
    /**
     * 更新讲师信息
     */
    Response updateTeacher(TeacherUpdateDTO teacherUpdateDTO);
    
    /**
     * 删除讲师
     */
    Response deleteTeacher(Long id);
    
    /**
     * 根据ID获取讲师详情
     */
    Response getTeacherById(Long id);
}

