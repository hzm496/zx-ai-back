package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.AIChatDTO;
import com.zm.zx.web.service.AIAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI学习助手控制器
 */
@RestController
@RequestMapping("/web/ai-assistant")
@RequiredArgsConstructor
@Slf4j
public class AIAssistantController {
    
    private final AIAssistantService aiAssistantService;
    
    /**
     * 创建新会话
     */
    @ApiOperationLog(description = "创建AI会话")
    @SaCheckLogin
    @PostMapping("/conversation/create")
    public Response createConversation() {
        Long userId = StpUtil.getLoginIdAsLong();
        Long conversationId = aiAssistantService.createConversation(userId);
        return Response.success(conversationId);
    }
    
    /**
     * 获取会话列表
     */
    @ApiOperationLog(description = "获取AI会话列表")
    @SaCheckLogin
    @GetMapping("/conversations")
    public Response getConversations() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(aiAssistantService.getConversations(userId));
    }
    
    /**
     * 获取会话消息
     */
    @ApiOperationLog(description = "获取AI会话消息")
    @SaCheckLogin
    @GetMapping("/messages/{conversationId}")
    public Response getMessages(@PathVariable("conversationId") Long conversationId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(aiAssistantService.getMessages(userId, conversationId));
    }
    
    /**
     * 发送消息
     */
    @ApiOperationLog(description = "发送AI消息")
    @SaCheckLogin
    @PostMapping("/chat")
    public Response chat(@Validated @RequestBody AIChatDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(aiAssistantService.chat(userId, dto.getConversationId(), dto.getMessage()));
    }
    
    /**
     * 删除会话
     */
    @ApiOperationLog(description = "删除AI会话")
    @SaCheckLogin
    @DeleteMapping("/conversation/{conversationId}")
    public Response deleteConversation(@PathVariable("conversationId") Long conversationId) {
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean result = aiAssistantService.deleteConversation(userId, conversationId);
        return result ? Response.success("删除成功") : Response.fail("删除失败");
    }
    
    /**
     * 重命名会话
     */
    @ApiOperationLog(description = "重命名AI会话")
    @SaCheckLogin
    @PutMapping("/conversation/{conversationId}/rename")
    public Response renameConversation(@PathVariable("conversationId") Long conversationId, 
                                        @RequestBody Map<String, String> request) {
        Long userId = StpUtil.getLoginIdAsLong();
        String newTitle = request.get("title");
        Boolean result = aiAssistantService.renameConversation(userId, conversationId, newTitle);
        return result ? Response.success("重命名成功") : Response.fail("重命名失败");
    }
}




