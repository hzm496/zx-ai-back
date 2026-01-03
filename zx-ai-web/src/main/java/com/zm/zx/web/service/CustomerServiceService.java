package com.zm.zx.web.service;

import com.zm.zx.web.domain.dto.SendMessageDTO;
import com.zm.zx.common.model.vo.CustomerServiceMessageVO;

import java.util.List;

/**
 * 客服服务接口
 */
public interface CustomerServiceService {
    
    /**
     * 用户发送消息
     */
    Boolean sendMessage(Long userId, SendMessageDTO dto);
    
    /**
     * 获取用户的聊天记录
     */
    List<CustomerServiceMessageVO> getMessages(Long userId, Integer limit);
    
    /**
     * 获取用户的未读消息数量
     */
    Integer getUnreadCount(Long userId);
    
    /**
     * 标记消息为已读
     */
    Boolean markAsRead(Long userId);
    
    /**
     * 清除会话历史记录
     */
    Boolean clearHistory(Long userId);
}


