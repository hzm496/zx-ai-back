package com.zm.zx.admin.service;

import com.zm.zx.admin.model.dto.FindVipOrderListDTO;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * VIP订单 Service
 */
public interface VipOrderService {
    
    /**
     * 查询VIP订单列表（分页）
     */
    PageResponse findVipOrderList(FindVipOrderListDTO findVipOrderListDTO);
    
    /**
     * 根据ID获取VIP订单详情
     */
    Response getVipOrderById(Long id);
    
    /**
     * 删除VIP订单
     */
    Response deleteVipOrder(Long id);
}

