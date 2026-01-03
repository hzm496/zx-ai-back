package com.zm.zx.web.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 章节 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterTreeVO {
    
    /**
     * 章节ID
     */
    private Long id;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 章节标题
     */
    private String title;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 视频时长（秒）
     */
    private Integer videoDuration;
    
    /**
     * 视频时长（格式化后的，如：10:30）
     */
    private String videoDurationFormat;
    
    /**
     * 父级章节ID，0表示章，非0表示节
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否免费试看：0-否，1-是
     */
    private Integer isFree;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
}

