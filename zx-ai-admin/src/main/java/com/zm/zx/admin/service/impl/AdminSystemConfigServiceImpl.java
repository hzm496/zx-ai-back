package com.zm.zx.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.admin.model.dto.SystemConfigUpdateDTO;
import com.zm.zx.admin.service.AdminSystemConfigService;
import com.zm.zx.common.model.po.SystemConfig;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.mapper.WebSystemConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSystemConfigServiceImpl implements AdminSystemConfigService {
    
    private final WebSystemConfigMapper configMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis 缓存 Key 前缀
    private static final String REDIS_CONFIG_PREFIX = "system:config:";
    
    @Override
    public Response getAllConfigs() {
        log.info("查询所有系统配置");
        
        // 查询所有配置
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SystemConfig::getConfigGroup)
            .orderByAsc(SystemConfig::getSortOrder);
            
        List<SystemConfig> configs = configMapper.selectList(wrapper);
        
        // 过滤掉不需要在系统设置中显示的配置（隐藏注册开关）
        configs = configs.stream()
            .filter(config -> !"register.enabled".equals(config.getConfigKey()) 
                && !"system.user_register_enabled".equals(config.getConfigKey()))
            .collect(Collectors.toList());
        
        // 按分组分类
        Map<String, List<SystemConfig>> groupedConfigs = configs.stream()
            .collect(Collectors.groupingBy(SystemConfig::getConfigGroup));
        
        // 转换为前端需要的格式
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("WEBSITE", groupedConfigs.getOrDefault("WEBSITE", List.of()));
        result.put("MAINTENANCE", groupedConfigs.getOrDefault("MAINTENANCE", List.of()));
        result.put("SYSTEM", groupedConfigs.getOrDefault("SYSTEM", List.of()));
        result.put("PAYMENT", groupedConfigs.getOrDefault("PAYMENT", List.of()));
        
        return Response.success(result);
    }
    
    @Override
    public Response getConfigByKey(String configKey) {
        log.info("查询配置：{}", configKey);
        
        // 先从Redis缓存查询
        String redisKey = REDIS_CONFIG_PREFIX + configKey;
        Object cachedValue = redisTemplate.opsForValue().get(redisKey);
        if (cachedValue != null) {
            return Response.success(cachedValue);
        }
        
        // Redis中没有，从数据库查询
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        
        SystemConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return Response.fail("配置不存在");
        }
        
        // 缓存到Redis（10秒，确保维护状态快速更新）
        redisTemplate.opsForValue().set(redisKey, config.getConfigValue(), 
            10, 
            java.util.concurrent.TimeUnit.SECONDS);
        
        return Response.success(config.getConfigValue());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response batchUpdateConfigs(List<SystemConfigUpdateDTO> configs) {
        log.info("批量更新配置，数量：{}", configs.size());
        
        for (SystemConfigUpdateDTO dto : configs) {
            // 查询配置
            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, dto.getConfigKey());
            
            SystemConfig config = configMapper.selectOne(wrapper);
            if (config != null) {
                // 更新配置值
                config.setConfigValue(dto.getConfigValue());
                configMapper.updateById(config);
                
                // 清除Redis缓存
                String redisKey = REDIS_CONFIG_PREFIX + dto.getConfigKey();
                redisTemplate.delete(redisKey);
            }
        }
        
        // 清除公开配置缓存
        String publicConfigsKey = "system:config:public:all";
        redisTemplate.delete(publicConfigsKey);
        
        log.info("批量配置更新成功");
        
        return Response.success("配置更新成功");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateConfig(SystemConfigUpdateDTO dto) {
        log.info("更新配置：{} = {}", dto.getConfigKey(), dto.getConfigValue());
        
        // 查询配置
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, dto.getConfigKey());
        
        SystemConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return Response.fail("配置不存在");
        }
        
        // 更新配置值
        config.setConfigValue(dto.getConfigValue());
        configMapper.updateById(config);
        
        // 清除Redis缓存
        String redisKey = REDIS_CONFIG_PREFIX + dto.getConfigKey();
        redisTemplate.delete(redisKey);
        
        // 清除公开配置缓存（供前台使用）
        String publicConfigsKey = "system:config:public:all";
        redisTemplate.delete(publicConfigsKey);
        
        log.info("配置更新成功：{}", dto.getConfigKey());
        
        return Response.success("更新成功");
    }
}

