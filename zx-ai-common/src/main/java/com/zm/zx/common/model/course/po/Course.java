package com.zm.zx.common.model.course.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("course")
public class Course {
    
    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
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
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 课程大纲
     */
    private String outline;
    
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
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 状态：0-下架，1-上架，2-审核中
     */
    private Integer status;
    
    /**
     * 是否支持试看：0-否，1-是
     */
    private Integer isTrial;
    
    /**
     * 试看章节数
     */
    private Integer trialChapterCount;

    /**
     * 课程的章节数（非数据库字段，查询时动态计算）
     */
    @TableField(exist = false)
    private Integer totalChapterCount;

    /**
     * 排序
     */
    private Integer sort;
    
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

