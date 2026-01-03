package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.web.domain.dto.SendMessageDTO;
import com.zm.zx.common.model.customer.po.CustomerServiceMessage;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.common.model.vo.CustomerServiceMessageVO;
import com.zm.zx.web.mapper.WebCustomerServiceMessageMapper;
import com.zm.zx.web.mapper.UserMapper;
import com.zm.zx.web.service.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 客服服务实现
 */
@Slf4j(topic = "CustomerService")
@Service
@RequiredArgsConstructor
public class CustomerServiceServiceImpl implements CustomerServiceService {
    
    private final WebCustomerServiceMessageMapper messageMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis Key 前缀
    private static final String REDIS_KEY_PREFIX = "customer_service:messages:";
    
    @Override
    public Boolean sendMessage(Long userId, SendMessageDTO dto) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        
        // 构建消息
        CustomerServiceMessage message = CustomerServiceMessage.builder()
            .userId(userId)
            .senderType(1) // 1-用户
            .senderName(user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "用户")
            .senderAvatar(user != null ? user.getAvatar() : null)
            .content(dto.getContent())
            .isRead(0)
            .createTime(LocalDateTime.now())
            .build();
        
        // 1. 保存到MySQL
        boolean success = messageMapper.insert(message) > 0;
        
        // 2. 同步添加到Redis List
        if (success) {
            String redisKey = REDIS_KEY_PREFIX + userId;
            try {
                // 转换为VO
                CustomerServiceMessageVO vo = new CustomerServiceMessageVO();
                BeanUtils.copyProperties(message, vo);
                
                // PUSH到Redis List的右侧（最新消息在最后）
                redisTemplate.opsForList().rightPush(redisKey, vo);
                // 刷新过期时间
                redisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
                
                log.debug("消息已追加到Redis List，userId: {}", userId);
            } catch (Exception e) {
                log.error("追加消息到Redis失败", e);
            }
        }
        
        return success;
    }
    
    @Override
    public List<CustomerServiceMessageVO> getMessages(Long userId, Integer limit) {
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
                    return cachedMessages;
                }
            }
        } catch (Exception e) {
            log.error("从Redis获取消息失败", e);
        }
        
        // 2. Redis没有，从MySQL查询
        LambdaQueryWrapper<CustomerServiceMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerServiceMessage::getUserId, userId)
            .orderByDesc(CustomerServiceMessage::getCreateTime)
            .last("LIMIT " + (limit != null ? limit : 50));
        
        List<CustomerServiceMessage> messages = messageMapper.selectList(wrapper);
        
        // 转换为VO并倒序（最早的消息在上面）
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
        
        return voList;
    }
    
    @Override
    public Integer getUnreadCount(Long userId) {
        // 获取客服发给用户的未读消息数量（sender_type=2）
        return messageMapper.getUnreadCount(userId, 2);
    }
    
    @Override
    public Boolean markAsRead(Long userId) {
        // 标记客服发给用户的消息为已读（sender_type=2）
        return messageMapper.markAsRead(userId, 2) >= 0;
    }
    
    @Override
    public Boolean clearHistory(Long userId) {
        // 1. 删除MySQL中的聊天记录
        LambdaQueryWrapper<CustomerServiceMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerServiceMessage::getUserId, userId);
        boolean success = messageMapper.delete(wrapper) >= 0;
        
        // 2. 删除Redis缓存
        if (success) {
            String redisKey = REDIS_KEY_PREFIX + userId;
            try {
                redisTemplate.delete(redisKey);
                log.debug("删除Redis缓存，userId: {}", userId);
            } catch (Exception e) {
                log.error("删除Redis缓存失败", e);
            }
        }
        
        return success;
    }
}


