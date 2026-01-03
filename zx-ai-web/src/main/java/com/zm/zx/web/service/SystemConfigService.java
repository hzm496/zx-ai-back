package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

/**
 * 系统配置服务接口（前台）
 */
public interface SystemConfigService {
    
    /**
     * 查询所有公开的系统配置
     */
    Response getPublicConfigs();
    
    /**
     * 根据配置键查询配置值（只能查询公开的配置）
     */
    Response getConfigByKey(String configKey);
}


