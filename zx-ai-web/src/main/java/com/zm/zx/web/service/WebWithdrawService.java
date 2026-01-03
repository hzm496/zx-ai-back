package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.WithdrawCreateDTO;

/**
 * 提现服务接口（前台用户端）
 */
public interface WebWithdrawService {
    
    /**
     * 创建提现申请
     * 
     * @param dto 提现申请DTO
     * @return 提现订单信息
     */
    Response createWithdrawOrder(WithdrawCreateDTO dto);
    
    /**
     * 获取提现订单列表（分页）
     * 
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param status 状态筛选（可选）
     * @return 提现订单列表
     */
    Response getWithdrawOrderList(Integer pageNo, Integer pageSize, Integer status);
    
    /**
     * 根据ID获取提现订单详情
     * 
     * @param id 提现订单ID
     * @return 订单详情
     */
    Response getWithdrawOrderById(Long id);
    
    /**
     * 取消提现申请（仅待处理状态可取消）
     * 
     * @param id 提现订单ID
     * @return 取消结果
     */
    Response cancelWithdrawOrder(Long id);
}

