package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

/**
 * Web端 - 讲师Service接口
 */
public interface WebTeacherService {
    
    /**
     * 获取所有讲师列表（只返回正常状态，按排序字段排序）
     */
    Response findAllTeachers();
    
    /**
     * 分页查询讲师列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     */
    Response getTeacherListByPage(Integer pageNo, Integer pageSize);
}

