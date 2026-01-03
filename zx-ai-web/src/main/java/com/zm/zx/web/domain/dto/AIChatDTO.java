package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI对话 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIChatDTO {
    
    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;
    
    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}




