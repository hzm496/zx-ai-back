package com.zm.zx.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询VIP订单列表 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindVipOrderListDTO {
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单类型（固定为2 - VIP订单）
     */
    private Integer type = 2;
    
    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;
}

