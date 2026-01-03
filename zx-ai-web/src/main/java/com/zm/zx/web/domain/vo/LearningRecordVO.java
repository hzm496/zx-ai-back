package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习记录 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LearningRecordVO {
    
    /**
     * 记录ID
     */
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
     * 课程标题
     */
    private String courseTitle;
    
    /**
     * 课程封面
     */
    private String courseCover;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 章节标题
     */
    private String chapterTitle;
    
    /**
     * 观看进度（秒）
     */
    private Integer progress;
    
    /**
     * 章节总时长（秒）
     */
    private Integer duration;
    
    /**
     * 学习进度百分比
     */
    private Integer progressPercent;
    
    /**
     * 是否看完：0-未完成，1-已完成
     */
    private Integer isFinished;
    
    /**
     * 最后学习时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLearnTime;
}

