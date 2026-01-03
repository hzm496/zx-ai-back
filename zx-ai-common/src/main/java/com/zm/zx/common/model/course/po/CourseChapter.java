package com.zm.zx.common.model.course.po;

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
 * 课程章节表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("course_chapter")
public class CourseChapter {
    
    /**
     * 章节ID
     */
    @TableId(type = IdType.AUTO)
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

