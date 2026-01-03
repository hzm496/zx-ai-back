package com.zm.zx.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.model.vo.AIConversationVO;
import com.zm.zx.common.model.vo.AIMessageVO;
import com.zm.zx.web.domain.po.AIConversation;
import com.zm.zx.web.domain.po.AIMessage;
import com.zm.zx.web.mapper.AIConversationMapper;
import com.zm.zx.web.mapper.AIMessageMapper;
import com.zm.zx.web.service.AIAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AI学习助手服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIAssistantServiceImpl implements AIAssistantService {
    
    private final AIConversationMapper conversationMapper;
    private final AIMessageMapper messageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatClient.Builder chatClientBuilder;
    
    // Redis Key 前缀：ai_chat:{userId}:{conversationId}
    private static final String REDIS_KEY_PREFIX = "ai_chat:";
    
    // 系统提示词
    private static final String SYSTEM_PROMPT = """
你是智学AI在线教育平台的智能学习助手，名字叫"智学小助手"。你是一位经验丰富、耐心细致的AI老师，专注于帮助学生解决学习问题并提供个性化的课程推荐。

## 核心能力
1. 学习答疑 - 回答编程、技术相关的问题（Java、Python、前端、后端、AI、数据库等）
2. 课程推荐 - 根据学生的学习目标、基础水平推荐合适的课程
3. 学习指导 - 制定学习计划、提供学习方法和技巧

## 回复风格
- 友好、耐心、专业
- 使用"你"而不是"您"
- 适当使用emoji（如👋😊📚等）
- 条理清晰，重要内容加粗
- 简洁明了，不要过长

## 平台课程分类
- 前端开发：HTML/CSS、JavaScript、Vue、React
- 后端开发：Java、Python、Go
- 移动开发：Flutter、React Native
- 人工智能：机器学习、深度学习、计算机视觉
- 云计算：Docker、Kubernetes
- 数据库：MySQL、Redis

请用简单易懂的语言回答学生的问题，在合适的时候推荐课程。
""";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConversation(Long userId) {
        AIConversation conversation = AIConversation.builder()
            .userId(userId)
            .title("新对话")
            .messageCount(0)
            .createTime(LocalDateTime.now())
            .build();
        
        conversationMapper.insert(conversation);
        return conversation.getId();
    }
    
    @Override
    public List<AIConversationVO> getConversations(Long userId) {
        LambdaQueryWrapper<AIConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIConversation::getUserId, userId)
            .orderByDesc(AIConversation::getUpdateTime);
        
        List<AIConversation> conversations = conversationMapper.selectList(wrapper);
        
        return conversations.stream()
            .map(conv -> {
                AIConversationVO vo = new AIConversationVO();
                BeanUtils.copyProperties(conv, vo);
                return vo;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AIMessageVO> getMessages(Long userId, Long conversationId) {
        String redisKey = REDIS_KEY_PREFIX + userId + ":" + conversationId;
        
        // 1. 先从Redis查询
        try {
            Long size = redisTemplate.opsForList().size(redisKey);
            if (size != null && size > 0) {
                List<Object> cachedObjects = redisTemplate.opsForList().range(redisKey, 0, -1);
                if (cachedObjects != null && !cachedObjects.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    List<AIMessageVO> cachedMessages = cachedObjects.stream()
                        .map(obj -> (AIMessageVO) obj)
                        .collect(Collectors.toList());
                    
                    log.debug("从Redis获取AI消息，key: {}", redisKey);
                    return cachedMessages;
                }
            }
        } catch (Exception e) {
            log.error("从Redis获取AI消息失败", e);
        }
        
        // 2. Redis没有，从MySQL查询
        LambdaQueryWrapper<AIMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIMessage::getConversationId, conversationId)
            .eq(AIMessage::getUserId, userId)
            .orderByAsc(AIMessage::getCreateTime);
        
        List<AIMessage> messages = messageMapper.selectList(wrapper);
        
        List<AIMessageVO> voList = messages.stream()
            .map(msg -> {
                AIMessageVO vo = new AIMessageVO();
                BeanUtils.copyProperties(msg, vo);
                return vo;
            })
            .collect(Collectors.toList());
        
        // 3. 缓存到Redis（30分钟过期）
        try {
            if (!voList.isEmpty()) {
                redisTemplate.delete(redisKey);
                redisTemplate.opsForList().rightPushAll(redisKey, voList.toArray());
                redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
                log.debug("缓存AI消息到Redis，key: {}", redisKey);
            }
        } catch (Exception e) {
            log.error("缓存AI消息到Redis失败", e);
        }
        
        return voList;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AIMessageVO chat(Long userId, Long conversationId, String userMessage) {
        String redisKey = REDIS_KEY_PREFIX + userId + ":" + conversationId;
        
        // 1. 保存用户消息到MySQL
        AIMessage userMsg = AIMessage.builder()
            .conversationId(conversationId)
            .userId(userId)
            .role("user")
            .content(userMessage)
            .createTime(LocalDateTime.now())
            .build();
        messageMapper.insert(userMsg);
        
        // 2. 获取历史消息构建上下文
        List<AIMessageVO> history = getMessages(userId, conversationId);
        List<Message> messages = new ArrayList<>();
        
        // 添加历史对话（最近10条）
        int startIndex = Math.max(0, history.size() - 10);
        for (int i = startIndex; i < history.size(); i++) {
            AIMessageVO msg = history.get(i);
            if ("user".equals(msg.getRole())) {
                messages.add(new UserMessage(msg.getContent()));
            } else {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }
        
        // 3. 调用AI获取回复（非流式，用于保存到数据库）
        ChatClient chatClient = chatClientBuilder.build();
        String aiResponse = chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user(userMessage)
            .call()
            .content();
        
        // 4. 保存AI回复到MySQL
        AIMessage aiMsg = AIMessage.builder()
            .conversationId(conversationId)
            .userId(userId)
            .role("assistant")
            .content(aiResponse)
            .createTime(LocalDateTime.now())
            .build();
        messageMapper.insert(aiMsg);
        
        // 5. 更新会话信息
        AIConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setLastMessage(aiResponse.length() > 50 ? aiResponse.substring(0, 50) + "..." : aiResponse);
            conversation.setMessageCount(conversation.getMessageCount() + 2);
            conversation.setUpdateTime(LocalDateTime.now());
            
            // 自动生成会话标题（第一次对话时）
            if ("新对话".equals(conversation.getTitle()) && conversation.getMessageCount() == 2) {
                String title = userMessage.length() > 20 ? userMessage.substring(0, 20) + "..." : userMessage;
                conversation.setTitle(title);
            }
            
            conversationMapper.updateById(conversation);
        }
        
        // 6. 清除Redis缓存，下次查询时重新加载
        try {
            redisTemplate.delete(redisKey);
            log.debug("已清除Redis缓存，key: {}", redisKey);
        } catch (Exception e) {
            log.error("清除Redis缓存失败", e);
        }
        
        // 7. 返回AI回复
        AIMessageVO vo = new AIMessageVO();
        BeanUtils.copyProperties(aiMsg, vo);
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConversation(Long userId, Long conversationId) {
        // 删除会话
        LambdaQueryWrapper<AIConversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(AIConversation::getId, conversationId)
            .eq(AIConversation::getUserId, userId);
        conversationMapper.delete(convWrapper);
        
        // 删除消息
        LambdaQueryWrapper<AIMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(AIMessage::getConversationId, conversationId)
            .eq(AIMessage::getUserId, userId);
        messageMapper.delete(msgWrapper);
        
        // 删除Redis缓存
        String redisKey = REDIS_KEY_PREFIX + userId + ":" + conversationId;
        try {
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            log.error("删除Redis缓存失败", e);
        }
        
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renameConversation(Long userId, Long conversationId, String newTitle) {
        AIConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null && conversation.getUserId().equals(userId)) {
            conversation.setTitle(newTitle);
            return conversationMapper.updateById(conversation) > 0;
        }
        return false;
    }
}




