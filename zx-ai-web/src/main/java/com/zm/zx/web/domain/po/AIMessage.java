package com.zm.zx.web.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI消息表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("ai_message")
public class AIMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long conversationId;
    
    private Long userId;
    
    /**
     * 角色：user-用户，assistant-AI助手
     */
    private String role;
    
    private String content;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}




