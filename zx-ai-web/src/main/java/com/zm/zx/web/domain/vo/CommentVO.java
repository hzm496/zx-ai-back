package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论展示 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentVO {
    
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 评分：1-5星
     */
    private Integer rating;
    
    /**
     * 父评论ID
     */
    private Long parentId;
    
    /**
     * 回复的用户ID
     */
    private Long replyToUserId;
    
    /**
     * 回复的用户昵称
     */
    private String replyToNickname;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 回复数量（仅一级评论有）
     */
    private Integer replyCount;
    
    /**
     * 第一条回复（仅一级评论有）
     */
    private CommentVO firstReply;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

