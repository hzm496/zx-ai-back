package com.zm.zx.web.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 创建评论请求 DTO
 */
@Data
public class CommentCreateDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    /**
     * 评分：1-5星（可选）
     */
    private Integer rating;
    
    /**
     * 父评论ID（0表示一级评论，大于0表示回复）
     */
    private Long parentId;
    
    /**
     * 回复的用户ID（如果是回复某人）
     */
    private Long replyToUserId;
}

