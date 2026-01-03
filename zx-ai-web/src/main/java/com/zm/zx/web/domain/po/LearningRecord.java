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
 * 学习记录表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("learning_record")
public class LearningRecord {
    
    /**
     * 记录ID
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
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 观看进度（秒）
     */
    private Integer progress;
    
    /**
     * 章节总时长（秒）
     */
    private Integer duration;
    
    /**
     * 是否看完：0-未完成，1-已完成
     */
    private Integer isFinished;
    
    /**
     * 最后学习时间
     */
    @TableField("last_learn_time")
    private LocalDateTime lastLearnTime;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}

