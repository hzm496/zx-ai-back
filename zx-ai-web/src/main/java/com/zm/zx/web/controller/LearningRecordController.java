package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.SaveLearningRecordDTO;
import com.zm.zx.web.service.LearningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 学习记录控制器
 */
@RestController
@RequestMapping("/web/learning")
@RequiredArgsConstructor
@Slf4j
public class LearningRecordController {
    
    private final LearningRecordService learningRecordService;
    
    /**
     * 保存学习记录
     */
    @ApiOperationLog(description = "保存学习记录")
    @SaCheckLogin
    @PostMapping("/record/save")
    public Response saveLearningRecord(@Validated @RequestBody SaveLearningRecordDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean result = learningRecordService.saveLearningRecord(userId, dto);
        return result ? Response.success("保存成功") : Response.fail("保存失败");
    }
    
    /**
     * 获取用户的学习记录列表
     */
    @ApiOperationLog(description = "获取学习记录列表")
    @SaCheckLogin
    @GetMapping("/records")
    public Response getLearningRecords() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(learningRecordService.getLearningRecords(userId));
    }
    
    /**
     * 获取用户在某个课程的最新学习记录
     */
    @ApiOperationLog(description = "获取课程学习记录")
    @SaCheckLogin
    @GetMapping("/record/course/{courseId}")
    public Response getLearningRecordByCourse(@PathVariable("courseId") Long courseId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(learningRecordService.getLearningRecordByCourse(userId, courseId));
    }
    
    /**
     * 获取用户在某个章节的学习记录
     */
    @ApiOperationLog(description = "获取章节学习记录")
    @SaCheckLogin
    @GetMapping("/record/chapter")
    public Response getLearningRecordByChapter(
            @RequestParam("courseId") Long courseId,
            @RequestParam("chapterId") Long chapterId
    ) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(learningRecordService.getLearningRecordByChapter(userId, courseId, chapterId));
    }
}

