package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RechargeCreateDTO;

/**
 * 充值服务接口
 */
public interface RechargeService {
    
    /**
     * 创建充值订单
     * 
     * @param dto 充值订单创建DTO
     * @return 订单信息
     */
    Response createRechargeOrder(RechargeCreateDTO dto);
    
    /**
     * 获取充值订单列表（分页）
     * 
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 充值订单列表
     */
    Response getRechargeOrderList(Integer pageNo, Integer pageSize);
    
    /**
     * 根据订单号获取充值订单详情
     * 
     * @param orderNo 订单号
     * @return 订单详情
     */
    Response getRechargeOrderByOrderNo(String orderNo);
    
    /**
     * 处理充值成功回调（更新订单状态和钱包余额）
     * 
     * @param orderNo 订单号
     * @param tradeNo 支付宝交易号
     * @return 处理结果
     */
    Response handleRechargeSuccess(String orderNo, String tradeNo);
}

