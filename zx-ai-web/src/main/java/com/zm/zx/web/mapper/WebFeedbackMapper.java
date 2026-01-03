package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zm.zx.common.model.feedback.dto.FindFeedbackListDTO;
import com.zm.zx.common.model.feedback.po.Feedback;
import com.zm.zx.common.model.feedback.vo.FeedbackVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 反馈Mapper接口
 */
@Mapper
public interface WebFeedbackMapper extends BaseMapper<Feedback> {
    
    /**
     * 分页查询反馈列表
     */
    IPage<FeedbackVO> findFeedbackList(IPage<FeedbackVO> page, @Param("dto") FindFeedbackListDTO dto);
    
    /**
     * 获取未读反馈数量
     */
    Integer getUnreadCount();
}

