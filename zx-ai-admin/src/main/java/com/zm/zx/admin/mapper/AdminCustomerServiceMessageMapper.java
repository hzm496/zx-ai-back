package com.zm.zx.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.customer.po.CustomerServiceMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Admin端 - 客服消息 Mapper
 */
@Mapper
public interface AdminCustomerServiceMessageMapper extends BaseMapper<CustomerServiceMessage> {
    
    /**
     * 获取所有有消息的用户ID列表
     */
    List<Long> getAllUserIds();
    
    /**
     * 获取用户的最后一条消息
     */
    CustomerServiceMessage getLastMessage(@Param("userId") Long userId);
    
    /**
     * 获取用户发送的未读消息数量（用户发给客服的）
     */
    int getUnreadCountByUser(@Param("userId") Long userId);
    
    /**
     * 获取所有用户的未读消息总数（用户发给客服的）
     */
    int getTotalUnreadCount();
    
    /**
     * 标记指定用户发送的消息为已读（用户发给客服的）
     */
    int markUserMessagesAsRead(@Param("userId") Long userId);
}

