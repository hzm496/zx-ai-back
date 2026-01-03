package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.SystemConfigUpdateDTO;
import com.zm.zx.admin.service.AdminSystemConfigService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器（管理端）
 */
@SaCheckRole("admin")
@RestController
@RequestMapping("/admin/system-config")
@RequiredArgsConstructor
@Slf4j
public class AdminSystemConfigController {
    
    private final AdminSystemConfigService systemConfigService;
    
    /**
     * 查询所有系统配置（按分组分类）
     */
    @ApiOperationLog(description = "查询所有系统配置")
    @SaCheckLogin
    @GetMapping("/all")
    public Response getAllConfigs() {
        return systemConfigService.getAllConfigs();
    }
    
    /**
     * 根据配置键查询配置值
     */
    @ApiOperationLog(description = "查询单个配置")
    @SaCheckLogin
    @GetMapping("/get")
    public Response getConfigByKey(@RequestParam("configKey") String configKey) {
        return systemConfigService.getConfigByKey(configKey);
    }
    
    /**
     * 批量更新配置
     */
    @ApiOperationLog(description = "批量更新配置")
    @SaCheckLogin
    @PostMapping("/batch-update")
    public Response batchUpdateConfigs(@Validated @RequestBody List<SystemConfigUpdateDTO> configs) {
        return systemConfigService.batchUpdateConfigs(configs);
    }
    
    /**
     * 更新单个配置
     */
    @ApiOperationLog(description = "更新单个配置")
    @SaCheckLogin
    @PostMapping("/update")
    public Response updateConfig(@Validated @RequestBody SystemConfigUpdateDTO dto) {
        return systemConfigService.updateConfig(dto);
    }
}

