package com.zm.zx.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.model.feedback.dto.FeedbackSubmitDTO;
import com.zm.zx.common.model.feedback.dto.FindFeedbackListDTO;
import com.zm.zx.common.model.feedback.po.Feedback;
import com.zm.zx.common.model.feedback.vo.FeedbackVO;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.mapper.WebFeedbackMapper;
import com.zm.zx.web.service.WebFeedbackService;
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
public class WebFeedbackServiceImpl extends ServiceImpl<WebFeedbackMapper, Feedback> implements WebFeedbackService {
    
    private final WebFeedbackMapper feedbackMapper;
    
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

