package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.CategoryAddDTO;
import com.zm.zx.admin.model.dto.CategoryUpdateDTO;
import com.zm.zx.admin.service.AdminCourseCategoryService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Admin - 课程分类管理Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/category")
public class CourseCategoryController {
    
    private final AdminCourseCategoryService categoryService;
    
    /**
     * 获取所有分类列表
     */
    @ApiOperationLog(description = "获取分类列表")
    @GetMapping("/list")
    public Response list() {
        return categoryService.findAllCategories();
    }
    
    /**
     * 添加分类
     */
    @ApiOperationLog(description = "添加分类")
    @PostMapping("/add")
    public Response add(@Validated @RequestBody CategoryAddDTO categoryAddDTO) {
        return categoryService.addCategory(categoryAddDTO);
    }
    
    /**
     * 更新分类信息
     */
    @ApiOperationLog(description = "更新分类信息")
    @PutMapping("/update")
    public Response update(@Validated @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        return categoryService.updateCategory(categoryUpdateDTO);
    }
    
    /**
     * 删除分类
     */
    @ApiOperationLog(description = "删除分类")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long id) {
        return categoryService.deleteCategory(id);
    }
    
    /**
     * 根据ID获取分类详情
     */
    @ApiOperationLog(description = "获取分类详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }
}

