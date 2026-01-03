package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.FeedbackSubmitDTO;
import com.zm.zx.admin.model.dto.FindFeedbackListDTO;
import com.zm.zx.common.model.feedback.po.Feedback;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * 反馈Service接口
 */
public interface FeedbackService extends IService<Feedback> {
    
    /**
     * 用户提交反馈
     */
    Response submitFeedback(FeedbackSubmitDTO dto, Long userId);
    
    /**
     * 分页查询反馈列表
     */
    PageResponse findFeedbackList(FindFeedbackListDTO dto);
    
    /**
     * 标记为已读
     */
    Response markAsRead(Long id);
    
    /**
     * 回复反馈
     */
    Response replyFeedback(Long id, String reply);
    
    /**
     * 获取未读反馈数量
     */
    Response getUnreadCount();
    
    /**
     * 删除反馈
     */
    Response deleteFeedback(Long id);
}

