package com.zm.zx.auth;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限验证接口实现
 * 用于告诉 Sa-Token 框架，当前用户拥有哪些权限和角色
 */
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合 
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 暂时不使用细粒度权限，返回空列表
        return List.of();
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     * 这个方法很重要！Sa-Token 通过这个方法获取用户角色，用于 @SaCheckRole 注解验证
     * 
     * 实现方式：直接从 Redis 读取角色（登录时已存储）
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            // 从 Redis 直接读取角色
            // key: satoken:login:role:{userId}
            String key = "satoken:login:role:" + loginId;
            String role = SaManager.getSaTokenDao().get(key);
            
            if (role != null && !role.isEmpty()) {
                List<String> roles = new ArrayList<>();
                roles.add(role);
                
                log.debug("从Redis获取用户 {} 的角色：{}", loginId, role);
                return roles;
            }
            
            log.warn("用户 {} 在Redis中没有角色信息", loginId);
            return List.of();
            
        } catch (Exception e) {
            log.error("从Redis获取用户角色失败，loginId: {}", loginId, e);
            return List.of();
        }
    }

}