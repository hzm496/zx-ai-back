package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.ChapterAddDTO;
import com.zm.zx.admin.model.dto.ChapterUpdateDTO;
import com.zm.zx.common.model.course.po.CourseChapter;
import com.zm.zx.common.response.Response;

/**
 * 课程章节Service接口
 */
public interface CourseChapterService extends IService<CourseChapter> {
    
    /**
     * 获取课程的章节列表（树形结构）
     */
    Response getChapterTree(Long courseId);
    
    /**
     * 添加章节
     */
    Response addChapter(ChapterAddDTO chapterAddDTO);
    
    /**
     * 更新章节
     */
    Response updateChapter(ChapterUpdateDTO chapterUpdateDTO);
    
    /**
     * 删除章节
     */
    Response deleteChapter(Long id);
    
    /**
     * 根据ID获取章节详情
     */
    Response getChapterById(Long id);
}

