package com.zm.zx.admin.service;

import com.zm.zx.common.response.Response;

/**
 * Admin端 - 客服服务接口
 */
public interface AdminCustomerServiceService {
    
    /**
     * 获取所有用户会话列表
     */
    Response getAllSessions();
    
    /**
     * 获取指定用户的聊天记录
     */
    Response getUserMessages(Long userId, Integer limit);
    
    /**
     * 客服回复用户消息
     */
    Response replyMessage(Long userId, String content);
    
    /**
     * 获取未读消息总数
     */
    Response getUnreadTotal();
}

