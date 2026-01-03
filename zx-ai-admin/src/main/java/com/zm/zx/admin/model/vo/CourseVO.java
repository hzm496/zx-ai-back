package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseVO {
    
    private Long id;
    private String title;
    private String subTitle;
    private String cover;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;
    private String description;
    private String outline;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer isFree;
    private Integer difficulty;
    private Integer duration;
    private Integer viewCount;
    private Integer buyCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
    private Integer isTrial;
    private Integer trialChapterCount;  // 试看章节数（从数据库实时统计）
    private Integer totalChapterCount;  // 总章节数（从数据库实时统计）
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

