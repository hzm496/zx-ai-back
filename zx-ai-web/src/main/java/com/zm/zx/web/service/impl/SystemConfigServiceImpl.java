package com.zm.zx.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.model.po.SystemConfig;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.mapper.WebSystemConfigMapper;
import com.zm.zx.web.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置服务实现类（前台）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {
    
    private final WebSystemConfigMapper configMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis 缓存 Key
    private static final String REDIS_CONFIG_PREFIX = "system:config:";
    private static final String REDIS_PUBLIC_CONFIGS = "system:config:public:all";
    
    @Override
    public Response getPublicConfigs() {
        log.info("查询所有公开配置");
        
        // 先从Redis查询
        Object cached = redisTemplate.opsForValue().get(REDIS_PUBLIC_CONFIGS);
        if (cached != null) {
            return Response.success(cached);
        }
        
        // 查询所有公开的配置
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getIsPublic, 1)
            .orderByAsc(SystemConfig::getSortOrder);
        
        List<SystemConfig> configs = configMapper.selectList(wrapper);
        
        // 转换为 key-value Map
        Map<String, String> result = new HashMap<>();
        for (SystemConfig config : configs) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        
        // 缓存到Redis（10秒，确保维护状态快速更新）
        redisTemplate.opsForValue().set(REDIS_PUBLIC_CONFIGS, result, 10, TimeUnit.SECONDS);
        
        return Response.success(result);
    }
    
    @Override
    public Response getConfigByKey(String configKey) {
        log.info("查询配置：{}", configKey);
        
        // 先从Redis查询
        String redisKey = REDIS_CONFIG_PREFIX + configKey;
        Object cachedValue = redisTemplate.opsForValue().get(redisKey);
        if (cachedValue != null) {
            return Response.success(cachedValue);
        }
        
        // 从数据库查询
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey)
            .eq(SystemConfig::getIsPublic, 1); // 只能查询公开的配置
        
        SystemConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return Response.fail("配置不存在或不可访问");
        }
        
        // 缓存到Redis（10秒，确保维护状态快速更新）
        redisTemplate.opsForValue().set(redisKey, config.getConfigValue(), 10, TimeUnit.SECONDS);
        
        return Response.success(config.getConfigValue());
    }
}

