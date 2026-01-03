package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 添加会员配置 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipAddDTO {
    
    /**
     * 会员名称
     */
    @NotBlank(message = "会员名称不能为空")
    private String name;
    
    /**
     * 时长（天）
     */
    @NotNull(message = "时长不能为空")
    private Integer duration;
    
    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
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

