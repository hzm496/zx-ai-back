package com.zm.zx.web.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * VIP套餐 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VipPackageVO {
    
    /**
     * 套餐类型：1-月卡，2-季卡，3-年卡
     */
    private Integer packageType;
    
    /**
     * 套餐名称
     */
    private String packageName;
    
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
     * 描述
     */
    private String description;
    
    /**
     * 是否推荐
     */
    private Boolean recommended;
}

