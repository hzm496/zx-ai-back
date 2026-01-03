package com.zm.zx.web.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RedisConfig {


    @Bean
    public RedissonClient redisClient() {
        RedissonClient redissonClient = null;
        // 获取config的实例
        Config config = new Config();
        // 设置请求的URL地址
        String url = "redis://81.71.143.129:6379";
        config.useSingleServer().setAddress(url);
        // 通过redisson创建一个客户端对象
        try {
            redissonClient = Redisson.create(config);
            log.info("创建RedissonClient成功...");
            return redissonClient;
        } catch (Exception e) {
            log.error("创建RedissonClient失败：" + e.fillInStackTrace());
            return null;
        }
    }
}
