package com.zm.zx.admin.model.dto;

import com.zm.zx.common.model.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询活动列表 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindActivityListDTO extends BaseQuery {
    
    /**
     * 活动标题（模糊查询）
     */
    private String title;
    
    /**
     * 活动类型：1-送会员，2-送优惠券
     */
    private Integer type;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}



