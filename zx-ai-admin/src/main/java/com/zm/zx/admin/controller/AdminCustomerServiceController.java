package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.model.vo.CustomerServiceMessageVO;
import com.zm.zx.common.response.Response;
import com.zm.zx.admin.service.AdminCustomerServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin端 - 客服控制器（管理端）
 */
@SaCheckRole("admin")
@RestController
@RequestMapping("/admin/customer-service")
@RequiredArgsConstructor
@Slf4j
public class AdminCustomerServiceController {
    
    private final AdminCustomerServiceService customerServiceService;
    
    /**
     * 获取所有用户会话列表
     */
//    @ApiOperationLog(description = "获取用户会话列表")
    @SaCheckLogin
    @GetMapping("/sessions")
    public Response getSessions() {
        return customerServiceService.getAllSessions();
    }
    
    /**
     * 获取指定用户的聊天记录
     */
//    @ApiOperationLog(description = "获取用户聊天记录")
    @SaCheckLogin
    @GetMapping("/messages/{userId}")
    public Response getUserMessages(@PathVariable("userId") Long userId, 
                                     @RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        return customerServiceService.getUserMessages(userId, limit);
    }
    
    /**
     * 客服回复用户消息
     */
    @ApiOperationLog(description = "客服回复消息")
    @SaCheckLogin
    @PostMapping("/reply/{userId}")
    public Response replyMessage(@PathVariable("userId") Long userId, 
                                  @Validated @RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Response.fail("消息内容不能为空");
        }
        return customerServiceService.replyMessage(userId, content);
    }
    
    /**
     * 获取未读消息总数（所有用户）
     */
    @ApiOperationLog(description = "获取未读消息总数")
    @SaCheckLogin
    @GetMapping("/unread-total")
    public Response getUnreadTotal() {
        return customerServiceService.getUnreadTotal();
    }
}

