package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;

/**
 * 会员配置 Service (Web前台)
 */
public interface WebMembershipService {
    
    /**
     * 获取会员配置列表（带Redis缓存）
     */
    Response getMembershipList();
}

