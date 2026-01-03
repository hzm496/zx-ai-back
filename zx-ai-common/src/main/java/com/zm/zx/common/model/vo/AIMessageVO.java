package com.zm.zx.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI消息 VO（通用）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIMessageVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long conversationId;
    private Long userId;
    
    /**
     * 角色：user-用户，assistant-AI助手
     */
    private String role;
    
    private String content;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}




