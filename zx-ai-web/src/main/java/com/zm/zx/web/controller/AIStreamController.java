package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.web.domain.dto.AIChatDTO;
import com.zm.zx.web.service.AIStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI流式对话控制器
 */
@RestController
@RequestMapping("/web/ai-assistant")
@RequiredArgsConstructor
@Slf4j
public class AIStreamController {
    
    private final AIStreamService aiStreamService;
    
    /**
     * 流式对话接口
     */
    @ApiOperationLog(description = "AI流式对话")
    @SaCheckLogin
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody AIChatDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return aiStreamService.chatStream(userId, dto.getConversationId(), dto.getMessage());
    }
}

