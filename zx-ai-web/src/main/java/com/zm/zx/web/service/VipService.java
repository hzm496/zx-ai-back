package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.VipOrderCreateDTO;
import com.zm.zx.web.domain.dto.VipPaymentDTO;

/**
 * VIP Service
 */
public interface VipService {
    
    /**
     * 获取VIP套餐列表
     * 
     * @return 套餐列表
     */
    Response getPackages();
    
    /**
     * 创建VIP订单
     * 
     * @param dto 创建订单DTO
     * @return 订单信息
     */
    Response createOrder(VipOrderCreateDTO dto);
    
    /**
     * 支付VIP订单
     * 
     * @param dto 支付DTO
     * @return 支付结果
     */
    Response payOrder(VipPaymentDTO dto);
    
    /**
     * 获取订单详情
     * 
     * @param orderNo 订单号
     * @return 订单详情
     */
    Response getOrderDetail(String orderNo);
    
    /**
     * 获取当前用户的VIP状态
     * 
     * @return VIP状态
     */
    Response getVipStatus();
    
    /**
     * 获取当前用户的VIP订单列表
     * 
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    Response getVipOrders(Integer pageNo, Integer pageSize);
}

