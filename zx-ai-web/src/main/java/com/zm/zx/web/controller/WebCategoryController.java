package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.WebCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Web - 课程分类Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/category")
public class WebCategoryController {
    
    private final WebCategoryService categoryService;
    
    /**
     * 获取所有分类列表（只返回正常状态）
     */
    @ApiOperationLog(description = "获取分类列表")
    @GetMapping("/list")
    public Response list() {
        return categoryService.findAllCategories();
    }
    
    /**
     * 获取顶级分类列表（parentId = 0，只返回正常状态）
     */
    @ApiOperationLog(description = "获取顶级分类列表")
    @GetMapping("/top")
    public Response getTopCategories() {
        return categoryService.getTopCategories();
    }
    
    /**
     * 根据ID获取分类详情（只返回正常状态）
     */
    @ApiOperationLog(description = "获取分类详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }
    
    /**
     * 获取热门分类列表（包含课程数量，按课程数量排序，最多返回8个）
     */
    @ApiOperationLog(description = "获取热门分类列表")
    @GetMapping("/hot")
    public Response getHotCategories() {
        return categoryService.getHotCategories();
    }
}

