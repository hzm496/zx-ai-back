package com.zm.zx.web.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论表
 * @TableName comment
 */
@TableName(value = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 父评论ID，0表示一级评论
     */
    private Long parentId;

    /**
     * 回复的用户ID
     */
    private Long replyToUserId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态：0-待审核，1-已通过，2-已删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

