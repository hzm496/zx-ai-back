package com.zm.zx.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 讲师 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherVO {
    
    /**
     * 讲师ID
     */
    private Long id;
    
    /**
     * 讲师姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 简介
     */
    private String intro;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 工作经历
     */
    private String experience;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

