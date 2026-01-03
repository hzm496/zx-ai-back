package com.zm.zx.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新会员配置 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipUpdateDTO {
    
    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空")
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
}

