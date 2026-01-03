package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.customer.po.CustomerServiceMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客服消息 Mapper（用户端）
 */
@Mapper
public interface WebCustomerServiceMessageMapper extends BaseMapper<CustomerServiceMessage> {
    
    /**
     * 获取用户的未读消息数量
     */
    int getUnreadCount(@Param("userId") Long userId, @Param("senderType") Integer senderType);
    
    /**
     * 标记用户的消息为已读
     */
    int markAsRead(@Param("userId") Long userId, @Param("senderType") Integer senderType);
}


