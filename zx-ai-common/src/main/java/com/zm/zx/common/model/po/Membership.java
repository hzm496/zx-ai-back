package com.zm.zx.common.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员配置表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("membership")
public class Membership implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 会员ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会员名称
     */
    private String name;
    
    /**
     * 时长（天）
     */
    private Integer duration;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 会员描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态：0-禁用，1-正常
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
}
