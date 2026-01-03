package com.zm.zx.web.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Web端 - 课程VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebCourseVO {
    
    /**
     * 课程ID
     */
    private Long id;
    
    /**
     * 课程标题
     */
    private String title;
    
    /**
     * 课程副标题
     */
    private String subTitle;
    
    /**
     * 封面图
     */
    private String cover;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 讲师名称
     */
    private String teacherName;
    
    /**
     * 讲师头像
     */
    private String teacherAvatar;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 是否免费：0-付费，1-免费
     */
    private Integer isFree;
    
    /**
     * 难度：1-入门，2-初级，3-中级，4-高级
     */
    private Integer difficulty;
    
    /**
     * 难度名称
     */
    private String difficultyName;
    
    /**
     * 总时长（分钟）
     */
    private Integer duration;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 购买人数
     */
    private Integer buyCount;
    
    /**
     * 课程总章节数（设定值）
     */
    private Integer totalChapterCount;
    
    /**
     * 当前实际章节数
     */
    private Integer chapterCount;
}

