package com.zm.zx.admin.model.dto;

import lombok.Data;

/**
 * 查询提现订单列表DTO
 */
@Data
public class FindWithdrawListDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 提现流水号
     */
    private String withdrawNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 状态：0-待处理，1-已完成，2-已拒绝
     */
    private Integer status;
}

