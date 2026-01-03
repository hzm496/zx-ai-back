package com.zm.zx.common.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统配置实体类（共享）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("system_config")
public class SystemConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 配置键（唯一）
     */
    private String configKey;
    
    /**
     * 配置值
     */
    private String configValue;
    
    /**
     * 配置类型：STRING-字符串，NUMBER-数字，BOOLEAN-布尔，JSON-JSON对象
     */
    private String configType;
    
    /**
     * 配置分组：SYSTEM-系统配置，WEBSITE-网站配置，MAINTENANCE-维护配置
     */
    private String configGroup;
    
    /**
     * 配置标签（中文名称）
     */
    private String configLabel;
    
    /**
     * 配置描述
     */
    private String configDesc;
    
    /**
     * 是否公开（0-否，1-是，公开配置前端可查询）
     */
    private Integer isPublic;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}


