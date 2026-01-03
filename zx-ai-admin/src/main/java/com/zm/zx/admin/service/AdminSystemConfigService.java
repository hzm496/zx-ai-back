package com.zm.zx.admin.service;

import com.zm.zx.admin.model.dto.SystemConfigUpdateDTO;
import com.zm.zx.common.response.Response;

import java.util.List;

/**
 * 系统配置服务接口
 */
public interface AdminSystemConfigService {
    
    /**
     * 查询所有配置（按分组分类）
     */
    Response getAllConfigs();
    
    /**
     * 根据配置键查询配置值
     */
    Response getConfigByKey(String configKey);
    
    /**
     * 批量更新配置
     */
    Response batchUpdateConfigs(List<SystemConfigUpdateDTO> configs);
    
    /**
     * 更新单个配置
     */
    Response updateConfig(SystemConfigUpdateDTO dto);
}

