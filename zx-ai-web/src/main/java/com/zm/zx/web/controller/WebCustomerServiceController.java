package com.zm.zx.web.controller;

import com.zm.zx.common.model.vo.CustomerServiceMessageVO;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.SendMessageDTO;
import com.zm.zx.web.service.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Web端 - 客服控制器（用户端）
 */
@RestController
@RequestMapping("/web/customer-service")
@RequiredArgsConstructor
@Slf4j
public class WebCustomerServiceController {
    
    private final CustomerServiceService customerServiceService;
    
    /**
     * 用户发送消息
     */
    @ApiOperationLog(description = "发送客服消息")
    @SaCheckLogin
    @PostMapping("/send")
    public Response sendMessage(@Validated @RequestBody SendMessageDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean result = customerServiceService.sendMessage(userId, dto);
        return result ? Response.success("发送成功") : Response.fail("发送失败");
    }
    
    /**
     * 获取聊天记录
     */
//    @ApiOperationLog(description = "获取聊天记录")
    @SaCheckLogin
    @GetMapping("/messages")
    public Response getMessages(@RequestParam(value = "limit", defaultValue = "50") Integer limit) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(customerServiceService.getMessages(userId, limit));
    }
    
    /**
     * 获取未读消息数量
     */
//    @ApiOperationLog(description = "获取未读消息数量")
    @SaCheckLogin
    @GetMapping("/unread-count")
    public Response getUnreadCount() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(customerServiceService.getUnreadCount(userId));
    }
    
    /**
     * 标记消息为已读
     */
    @ApiOperationLog(description = "标记消息为已读")
    @SaCheckLogin
    @PostMapping("/mark-read")
    public Response markAsRead() {
        Long userId = StpUtil.getLoginIdAsLong();
        customerServiceService.markAsRead(userId);
        return Response.success("标记成功");
    }
    
    /**
     * 清除会话历史
     */
    @ApiOperationLog(description = "清除会话历史")
    @SaCheckLogin
    @DeleteMapping("/clear")
    public Response clearHistory() {
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean result = customerServiceService.clearHistory(userId);
        return result ? Response.success("清除成功") : Response.fail("清除失败");
    }
}


