package com.zm.zx.web.service;

import reactor.core.publisher.Flux;

/**
 * AI流式对话服务
 */
public interface AIStreamService {
    
    /**
     * 流式对话
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param userMessage 用户消息
     * @return 流式响应
     */
    Flux<String> chatStream(Long userId, Long conversationId, String userMessage);
}

