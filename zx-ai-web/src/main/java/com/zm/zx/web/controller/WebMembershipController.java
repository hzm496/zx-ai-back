package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.WebMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员配置 Controller (Web前台)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/membership")
public class WebMembershipController {
    
    private final WebMembershipService webMembershipService;
    
    /**
     * 获取会员配置列表（带Redis缓存）
     */
    @ApiOperationLog(description = "获取会员配置列表")
    @GetMapping("/list")
    public Response list() {
        return webMembershipService.getMembershipList();
    }
}

