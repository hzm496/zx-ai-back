package com.zm.zx.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI会话 VO（通用）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIConversationVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private String title;
    private String lastMessage;
    private Integer messageCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}




