package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程章节 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseChapterVO {
    
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
     * 父级章节ID
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否免费试看
     */
    private Integer isFree;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 子章节列表
     */
    private List<CourseChapterVO> children;
}

