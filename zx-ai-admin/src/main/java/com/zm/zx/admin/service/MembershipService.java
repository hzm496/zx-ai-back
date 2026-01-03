package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.MembershipAddDTO;
import com.zm.zx.admin.model.dto.MembershipUpdateDTO;
import com.zm.zx.common.model.po.Membership;
import com.zm.zx.common.response.Response;

/**
 * 会员配置 Service
 */
public interface MembershipService extends IService<Membership> {
    
    /**
     * 获取会员配置列表
     */
    Response getMembershipList();
    
    /**
     * 添加会员配置
     */
    Response addMembership(MembershipAddDTO membershipAddDTO);
    
    /**
     * 更新会员配置
     */
    Response updateMembership(MembershipUpdateDTO membershipUpdateDTO);
    
    /**
     * 删除会员配置
     */
    Response deleteMembership(Long id);
    
    /**
     * 根据ID获取会员配置
     */
    Response getMembershipById(Long id);
}

