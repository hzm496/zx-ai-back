package com.zm.zx.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.admin.mapper.AdminCustomerServiceMessageMapper;
import com.zm.zx.admin.mapper.AdminUserMapper;
import com.zm.zx.common.model.customer.po.CustomerServiceMessage;
import com.zm.zx.admin.model.po.AdminUser;
import com.zm.zx.common.model.vo.CustomerServiceMessageVO;
import com.zm.zx.admin.model.vo.CustomerServiceSessionVO;
import com.zm.zx.admin.service.AdminCustomerServiceService;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Admin端 - 客服服务实现
 */
@Slf4j(topic = "AdminCustomerService")
@Service
@RequiredArgsConstructor
public class AdminCustomerServiceServiceImpl implements AdminCustomerServiceService {
    
    private final AdminCustomerServiceMessageMapper messageMapper;
    private final AdminUserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis Key 前缀
    private static final String REDIS_KEY_PREFIX = "customer_service:messages:";
    
    @Override
    public Response getAllSessions() {
        // 获取所有有消息的用户ID
        List<Long> userIds = messageMapper.getAllUserIds();
        
        List<CustomerServiceSessionVO> sessions = new ArrayList<>();
        for (Long userId : userIds) {
            AdminUser user = userMapper.selectById(userId);
            if (user == null) continue;
            
            // 获取最后一条消息
            CustomerServiceMessage lastMessage = messageMapper.getLastMessage(userId);
            
            // 获取未读消息数
            int unreadCount = messageMapper.getUnreadCountByUser(userId);
            
            CustomerServiceSessionVO session = CustomerServiceSessionVO.builder()
                .userId(userId)
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                .lastMessageTime(lastMessage != null ? lastMessage.getCreateTime() : null)
                .unreadCount(unreadCount)
                .build();
            
            sessions.add(session);
        }
        
        // 按最后消息时间倒序排列
        sessions.sort((a, b) -> {
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });
        
        return Response.success(sessions);
    }
    
    @Override
    public Response getUserMessages(Long userId, Integer limit) {
        String redisKey = REDIS_KEY_PREFIX + userId;
        
        // 1. 先从Redis List查询
        try {
            Long size = redisTemplate.opsForList().size(redisKey);
            if (size != null && size > 0) {
                // 获取所有消息（从左到右，即按时间正序）
                List<Object> cachedObjects = redisTemplate.opsForList().range(redisKey, 0, -1);
                if (cachedObjects != null && !cachedObjects.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    List<CustomerServiceMessageVO> cachedMessages = cachedObjects.stream()
                        .map(obj -> (CustomerServiceMessageVO) obj)
                        .collect(Collectors.toList());
                    
                    log.debug("从Redis获取消息，userId: {}, 数量: {}", userId, cachedMessages.size());
                    return Response.success(cachedMessages);
                }
            }
        } catch (Exception e) {
            log.error("从Redis获取消息失败", e);
        }
        
        // 2. Redis没有，从MySQL查询
        LambdaQueryWrapper<CustomerServiceMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerServiceMessage::getUserId, userId)
            .orderByDesc(CustomerServiceMessage::getCreateTime)
            .last("LIMIT " + (limit != null ? limit : 100));
        
        List<CustomerServiceMessage> messages = messageMapper.selectList(wrapper);
        
        // 转换为VO并倒序
        List<CustomerServiceMessageVO> voList = messages.stream()
            .map(msg -> {
                CustomerServiceMessageVO vo = new CustomerServiceMessageVO();
                BeanUtils.copyProperties(msg, vo);
                return vo;
            })
            .sorted((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()))
            .collect(Collectors.toList());
        
        // 3. 批量缓存到Redis List（10分钟过期）
        try {
            if (!voList.isEmpty()) {
                // 先删除旧数据
                redisTemplate.delete(redisKey);
                // 批量添加到List
                redisTemplate.opsForList().rightPushAll(redisKey, voList.toArray());
                // 设置过期时间
                redisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
                log.debug("缓存消息到Redis，userId: {}, 数量: {}", userId, voList.size());
            }
        } catch (Exception e) {
            log.error("缓存消息到Redis失败", e);
        }
        
        return Response.success(voList);
    }
    
    @Override
    public Response replyMessage(Long userId, String content) {
        // 获取当前管理员信息
        Long adminId = StpUtil.getLoginIdAsLong();
        AdminUser admin = userMapper.selectById(adminId);
        
        CustomerServiceMessage message = CustomerServiceMessage.builder()
            .userId(userId)
            .senderType(2) // 2-客服
            .senderName(admin != null ? "客服" + (admin.getNickname() != null ? admin.getNickname() : admin.getUsername()) : "客服")
            .senderAvatar(admin != null ? admin.getAvatar() : null)
            .content(content)
            .isRead(0)
            .createTime(LocalDateTime.now())
            .build();
        
        boolean success = messageMapper.insert(message) > 0;
        
        if (success) {
            // 回复成功后，将该用户发送给客服的所有未读消息标记为已读
            messageMapper.markUserMessagesAsRead(userId);
            
            // 同步添加到Redis List
            String redisKey = REDIS_KEY_PREFIX + userId;
            try {
                // 转换为VO
                CustomerServiceMessageVO vo = new CustomerServiceMessageVO();
                BeanUtils.copyProperties(message, vo);
                
                // PUSH到Redis List的右侧（最新消息在最后）
                redisTemplate.opsForList().rightPush(redisKey, vo);
                // 刷新过期时间
                redisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
                
                log.debug("客服回复已追加到Redis List，userId: {}", userId);
            } catch (Exception e) {
                log.error("追加消息到Redis失败", e);
            }
        }
        
        return success ? Response.success("发送成功") : Response.fail("发送失败");
    }
    
    @Override
    public Response getUnreadTotal() {
        int total = messageMapper.getTotalUnreadCount();
        return Response.success(total);
    }
}

