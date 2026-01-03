package com.zm.zx.web.service.impl;

import com.zm.zx.common.model.vo.AIMessageVO;
import com.zm.zx.web.domain.po.AIConversation;
import com.zm.zx.web.domain.po.AIMessage;
import com.zm.zx.web.function.CourseSearchFunction;
import com.zm.zx.web.mapper.AIConversationMapper;
import com.zm.zx.web.mapper.AIMessageMapper;
import com.zm.zx.web.service.AIAssistantService;
import com.zm.zx.web.service.AIStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI流式对话服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIStreamServiceImpl implements AIStreamService {
    
    private final AIConversationMapper conversationMapper;
    private final AIMessageMapper messageMapper;
    private final AIAssistantService aiAssistantService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatClient.Builder chatClientBuilder;
    private final CourseSearchFunction courseSearchFunction;
    
    // Redis Key 前缀
    private static final String REDIS_KEY_PREFIX = "ai_chat:";
    
    // 系统提示词
    private static final String SYSTEM_PROMPT = """
你是智学AI在线教育平台的智能学习助手，名字叫"智学小助手"。你是一位经验丰富、耐心细致的AI老师，专注于帮助学生解决学习问题并提供个性化的课程推荐。

## 核心能力
1. 学习答疑 - 回答编程、技术相关的问题（Java、Python、前端、后端、AI、数据库等）
2. 课程推荐 - 根据学生的学习目标、基础水平推荐合适的课程
3. 学习指导 - 制定学习计划、提供学习方法和技巧

## 重要规则：课程推荐
**推荐课程时必须从下方"平台课程列表"中选择真实存在的课程！**

推荐课程要求：
1. 只推荐"平台课程列表"中存在的课程
2. 必须使用《课程标题》格式（用《》包裹课程名），这样学生可以直接点击跳转
3. 根据学生的基础和目标推荐合适难度的课程
4. 如果列表中没有相关课程，诚实告知学生

示例：
- 用户："我想学Java"
- 你："推荐你学习《Java零基础入门到精通》，这门课程适合零基础学员..."

## 回复风格
- 友好、耐心、专业
- 使用"你"而不是"您"
- 适当使用emoji（如👋😊📚等）
- 条理清晰，重要内容用**加粗**
- 简洁明了，不要过长

请用简单易懂的语言回答学生的问题！
""";
    
    @Override
    public Flux<String> chatStream(Long userId, Long conversationId, String userMessage) {
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
        List<AIMessageVO> history = aiAssistantService.getMessages(userId, conversationId);
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
        
        // 3. 查询平台课程列表（供AI参考）
        String courseContext = buildCourseContext(userMessage);
        String fullSystemPrompt = SYSTEM_PROMPT + "\n\n" + courseContext;
        
        // 4. 调用AI流式API
        ChatClient chatClient = chatClientBuilder.build();
        Flux<String> responseFlux = chatClient.prompt()
            .system(fullSystemPrompt)
            .user(userMessage)
            .stream()
            .content();
        
        // 4. 收集完整回复并保存到数据库
        StringBuilder fullResponse = new StringBuilder();
        
        return responseFlux
            .doOnNext(chunk -> {
                fullResponse.append(chunk);
                log.debug("收到chunk: {}", chunk);
            })
            .doFinally(signalType -> {
                log.info("🔚 Flux结束信号: {}, 完整内容长度: {}", signalType, fullResponse.length());
                
                // 流式输出完成后，保存完整回复到MySQL
                try {
                    AIMessage aiMsg = AIMessage.builder()
                        .conversationId(conversationId)
                        .userId(userId)
                        .role("assistant")
                        .content(fullResponse.toString())
                        .createTime(LocalDateTime.now())
                        .build();
                    messageMapper.insert(aiMsg);
                    
                    // 更新会话信息
                    AIConversation conversation = conversationMapper.selectById(conversationId);
                    if (conversation != null) {
                        String lastMsg = fullResponse.toString();
                        conversation.setLastMessage(lastMsg.length() > 50 ? lastMsg.substring(0, 50) + "..." : lastMsg);
                        conversation.setMessageCount(conversation.getMessageCount() + 2);
                        conversation.setUpdateTime(LocalDateTime.now());
                        
                        // 自动生成会话标题
                        if ("新对话".equals(conversation.getTitle()) && conversation.getMessageCount() == 2) {
                            String title = userMessage.length() > 20 ? userMessage.substring(0, 20) + "..." : userMessage;
                            conversation.setTitle(title);
                        }
                        
                        conversationMapper.updateById(conversation);
                    }
                    
                    // 清除Redis缓存
                    String redisKey = REDIS_KEY_PREFIX + userId + ":" + conversationId;
                    redisTemplate.delete(redisKey);
                    
                    log.info("✅ AI消息已保存到数据库");
                } catch (Exception e) {
                    log.error("保存AI消息失败", e);
                }
            });
    }
    
    /**
     * 构建课程上下文 - 根据用户消息查询相关课程
     */
    private String buildCourseContext(String userMessage) {
        try {
            // 提取关键词（简单实现）
            String keyword = extractKeyword(userMessage);
            
            // 调用CourseSearchFunction查询课程
            CourseSearchFunction.Request request = new CourseSearchFunction.Request(
                keyword, 
                null,  // category
                10     // 查询10门课程
            );
            CourseSearchFunction.Response response = courseSearchFunction.apply(request);
            
            if (response.courses == null || response.courses.isEmpty()) {
                return "## 平台课程列表\n暂无相关课程";
            }
            
            // 构建课程列表文本
            StringBuilder context = new StringBuilder();
            context.append("## 平台课程列表（请只从以下课程中推荐，使用《课程标题》格式）\n\n");
            
            for (CourseSearchFunction.CourseInfo course : response.courses) {
                context.append(String.format("- 《%s》\n", course.title));
                context.append(String.format("  分类：%s | 难度：%s | 价格：%s | 购买：%d人\n", 
                    course.category, course.difficulty, course.price, course.buyCount));
                if (course.description != null && !course.description.isBlank()) {
                    String desc = course.description.length() > 100 ? 
                        course.description.substring(0, 100) + "..." : course.description;
                    context.append(String.format("  简介：%s\n", desc));
                }
                context.append("\n");
            }
            
            context.append("**重要：推荐课程时请使用《课程标题》格式，这样学生可以直接点击跳转！**\n");
            
            return context.toString();
        } catch (Exception e) {
            log.error("构建课程上下文失败", e);
            return "";
        }
    }
    
    /**
     * 从用户消息中提取关键词
     */
    private String extractKeyword(String userMessage) {
        // 简单实现：匹配常见技术关键词
        String[] keywords = {"Java", "Python", "前端", "后端", "Vue", "React", "Spring", "MySQL", "Redis"};
        for (String keyword : keywords) {
            if (userMessage.contains(keyword)) {
                return keyword;
            }
        }
        // 如果没有匹配到关键词，返回空字符串（查询所有课程）
        return "";
    }
}

