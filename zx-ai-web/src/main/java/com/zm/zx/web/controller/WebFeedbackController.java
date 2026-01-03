package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.model.feedback.dto.FeedbackSubmitDTO;
import com.zm.zx.common.response.Response;
import com.zm.zx.common.utils.LoginUserContextHolder;
import com.zm.zx.web.service.WebFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Web端 - 反馈Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/feedback")
public class WebFeedbackController {
    
    private final WebFeedbackService feedbackService;
    
    /**
     * 提交反馈
     */
    @ApiOperationLog(description = "提交反馈")
    @PostMapping("/submit")
    public Response submitFeedback(@Valid @RequestBody FeedbackSubmitDTO dto) {
        // 获取当前登录用户ID（如果已登录）
        Long userId = null;
        try {
            userId = LoginUserContextHolder.getUserId();
        } catch (Exception e) {
            // 未登录用户，userId 为 null
        }
        
        return feedbackService.submitFeedback(dto, userId);
    }
}

