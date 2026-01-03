package com.zm.zx.admin.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据统计表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("statistics")
public class Statistics {
    
    /**
     * 统计ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 统计日期
     */
    private LocalDate statDate;
    
    /**
     * 新增用户数
     */
    private Integer newUserCount;
    
    /**
     * 活跃用户数
     */
    private Integer activeUserCount;
    
    /**
     * 新增订单数
     */
    private Integer newOrderCount;
    
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 新增课程数
     */
    private Integer newCourseCount;
    
    /**
     * 学习时长（秒）
     */
    private Long learningDuration;
    
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

