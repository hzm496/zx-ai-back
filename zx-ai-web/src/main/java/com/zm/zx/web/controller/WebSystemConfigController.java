package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器（前台）
 */
@RestController
@RequestMapping("/web/system-config")
@RequiredArgsConstructor
@Slf4j
public class WebSystemConfigController {
    
    private final SystemConfigService systemConfigService;
    
    /**
     * 查询公开的系统配置（前台可访问）
     */
    @ApiOperationLog(description = "查询公开配置")
    @GetMapping("/public")
    public Response getPublicConfigs() {
        return systemConfigService.getPublicConfigs();
    }
    
    /**
     * 根据配置键查询配置值
     */
    @ApiOperationLog(description = "查询单个配置")
    @GetMapping("/get")
    public Response getConfigByKey(@RequestParam("configKey") String configKey) {
        return systemConfigService.getConfigByKey(configKey);
    }
}

