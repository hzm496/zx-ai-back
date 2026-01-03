package com.zm.zx.common.model.teacher.po;

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
 * 讲师表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("teacher")
public class Teacher {
    
    /**
     * 讲师ID
     */
    @TableId(type = IdType.AUTO)
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
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}

