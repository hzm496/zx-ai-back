package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.ChapterAddDTO;
import com.zm.zx.admin.model.dto.ChapterUpdateDTO;
import com.zm.zx.admin.service.CourseChapterService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 课程章节Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chapter")
public class CourseChapterController {
    
    private final CourseChapterService courseChapterService;
    
    /**
     * 获取课程的章节列表（树形结构）
     */
    @ApiOperationLog(description = "获取课程章节列表")
    @GetMapping("/tree/{courseId}")
    public Response getChapterTree(@PathVariable("courseId") Long courseId) {
        return courseChapterService.getChapterTree(courseId);
    }
    
    /**
     * 添加章节
     */
    @ApiOperationLog(description = "添加章节")
    @PostMapping("/add")
    public Response add(@Validated @RequestBody ChapterAddDTO chapterAddDTO) {
        return courseChapterService.addChapter(chapterAddDTO);
    }
    
    /**
     * 更新章节
     */
    @ApiOperationLog(description = "更新章节")
    @PutMapping("/update")
    public Response update(@Validated @RequestBody ChapterUpdateDTO chapterUpdateDTO) {
        return courseChapterService.updateChapter(chapterUpdateDTO);
    }
    
    /**
     * 删除章节
     */
    @ApiOperationLog(description = "删除章节")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long id) {
        return courseChapterService.deleteChapter(id);
    }
    
    /**
     * 根据ID获取章节详情
     */
    @ApiOperationLog(description = "获取章节详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return courseChapterService.getChapterById(id);
    }
}

