package com.zm.zx.web.service;

import com.zm.zx.common.model.vo.AIConversationVO;
import com.zm.zx.common.model.vo.AIMessageVO;

import java.util.List;

/**
 * AI学习助手服务接口
 */
public interface AIAssistantService {
    
    /**
     * 创建新会话
     */
    Long createConversation(Long userId);
    
    /**
     * 获取用户的会话列表
     */
    List<AIConversationVO> getConversations(Long userId);
    
    /**
     * 获取会话的消息列表
     */
    List<AIMessageVO> getMessages(Long userId, Long conversationId);
    
    /**
     * 发送消息并获取AI回复
     */
    AIMessageVO chat(Long userId, Long conversationId, String userMessage);
    
    /**
     * 删除会话
     */
    Boolean deleteConversation(Long userId, Long conversationId);
    
    /**
     * 重命名会话
     */
    Boolean renameConversation(Long userId, Long conversationId, String newTitle);
}
