package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.FindFeedbackListDTO;
import com.zm.zx.admin.model.dto.ReplyFeedbackDTO;
import com.zm.zx.admin.service.FeedbackService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/feedback")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    /**
     * 获取反馈列表（分页）
     */
    @ApiOperationLog(description = "获取反馈列表")
    @PostMapping("/list")
    public PageResponse list(@RequestBody FindFeedbackListDTO dto) {
        return feedbackService.findFeedbackList(dto);
    }
    
    /**
     * 标记为已读
     */
    @ApiOperationLog(description = "标记反馈为已读")
    @PutMapping("/read/{id}")
    public Response markAsRead(@PathVariable("id") Long id) {
        return feedbackService.markAsRead(id);
    }
    
    /**
     * 回复反馈
     */
    @ApiOperationLog(description = "回复反馈")
    @PutMapping("/reply/{id}")
    public Response replyFeedback(@PathVariable("id") Long id, @RequestBody ReplyFeedbackDTO dto) {
        return feedbackService.replyFeedback(id, dto.getReply());
    }
    
    /**
     * 获取未读反馈数量
     */
    @ApiOperationLog(description = "获取未读反馈数量")
    @GetMapping("/unread-count")
    public Response getUnreadCount() {
        return feedbackService.getUnreadCount();
    }
    
    /**
     * 删除反馈
     */
    @ApiOperationLog(description = "删除反馈")
    @DeleteMapping("/{id}")
    public Response deleteFeedback(@PathVariable("id") Long id) {
        return feedbackService.deleteFeedback(id);
    }
}

