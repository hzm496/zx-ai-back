package com.zm.zx.web.consumer;

import com.zm.zx.common.constant.MQConstants;
import com.zm.zx.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
@RocketMQMessageListener(consumerGroup = "zm_bs_group" + MQConstants.TOPIC_TEACHER_CLEAN, // Group 组
        topic = MQConstants.TOPIC_TEACHER_CLEAN // 消费的 Topic 主题
)
public class TeacherConsumer implements RocketMQListener<String> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(String msg) {
        redisTemplate.delete(RedisKey.TEACHER_LIST);
    }
}
