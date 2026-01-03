package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.admin.mapper.AdminFeedbackMapper;
import com.zm.zx.admin.model.dto.FeedbackSubmitDTO;
import com.zm.zx.admin.model.dto.FindFeedbackListDTO;
import com.zm.zx.common.model.feedback.po.Feedback;
import com.zm.zx.admin.model.vo.FeedbackVO;
import com.zm.zx.admin.service.FeedbackService;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.admin.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 反馈Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeedbackServiceImpl extends ServiceImpl<AdminFeedbackMapper, Feedback> implements FeedbackService {
    
    private final AdminFeedbackMapper feedbackMapper;
    private final EmailService emailService;
    
    @Override
    public Response submitFeedback(FeedbackSubmitDTO dto, Long userId) {
        Feedback feedback = BeanUtil.copyProperties(dto, Feedback.class);
        feedback.setUserId(userId);
        feedback.setIsRead(0); // 默认未读
        
        boolean saved = this.save(feedback);
        if (!saved) {
            throw new BizException(ResponseEnum.ADD_FAIL);
        }
        
        return Response.success("反馈提交成功，我们会尽快处理");
    }
    
    @Override
    public PageResponse findFeedbackList(FindFeedbackListDTO dto) {
        Integer pageSize = dto.getPageSize();
        Integer pageNum = dto.getPageNum();
        
        Page<FeedbackVO> page = new Page<>(pageNum, pageSize);
        IPage<FeedbackVO> feedbackPage = feedbackMapper.findFeedbackList(page, dto);
        
        List<FeedbackVO> records = feedbackPage.getRecords();
        
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }
        
        return PageResponse.success(records, pageNum, feedbackPage.getTotal(), pageSize);
    }
    
    @Override
    public Response markAsRead(Long id) {
        Feedback feedback = this.getById(id);
        if (feedback == null) {
            throw new BizException(ResponseEnum.DATA_NOT_FOUND);
        }
        
        feedback.setIsRead(1);
        boolean updated = this.updateById(feedback);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }
        
        return Response.success();
    }
    
    @Override
    public Response replyFeedback(Long id, String reply) {
        Feedback feedback = this.getById(id);
        if (feedback == null) {
            throw new BizException(ResponseEnum.DATA_NOT_FOUND);
        }
        
        feedback.setReply(reply);
        feedback.setIsRead(1); // 回复时自动标记为已读
        boolean updated = this.updateById(feedback);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }
        
        // 发送回复邮件到用户邮箱
        try {
            if (feedback.getEmail() != null && !feedback.getEmail().isEmpty()) {
                emailService.sendFeedbackReplyEmail(
                    feedback.getEmail(),
                    feedback.getUsername(),
                    feedback.getContent(),
                    reply
                );
                log.info("反馈回复邮件发送成功：feedbackId={}, email={}", id, feedback.getEmail());
            }
        } catch (Exception e) {
            log.error("发送回复邮件失败：feedbackId={}, error={}", id, e.getMessage(), e);
            // 邮件发送失败不影响回复功能，只记录日志
        }
        
        return Response.success();
    }
    
    @Override
    public Response getUnreadCount() {
        Integer count = feedbackMapper.getUnreadCount();
        return Response.success(count);
    }
    
    @Override
    public Response deleteFeedback(Long id) {
        boolean deleted = this.removeById(id);
        if (!deleted) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }
        
        return Response.success();
    }
}

