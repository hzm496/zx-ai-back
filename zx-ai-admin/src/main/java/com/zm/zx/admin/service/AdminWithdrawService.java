package com.zm.zx.admin.service;

import com.zm.zx.admin.model.dto.FindWithdrawListDTO;
import com.zm.zx.admin.model.dto.WithdrawProcessDTO;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;

/**
 * 提现管理服务接口（后台）
 */
public interface AdminWithdrawService {
    
    /**
     * 获取提现订单列表（分页）
     */
    PageResponse findWithdrawList(FindWithdrawListDTO findWithdrawListDTO);
    
    /**
     * 根据ID获取提现订单详情
     */
    Response getWithdrawById(Long id);
    
    /**
     * 处理提现申请（审核通过/拒绝）
     */
    Response processWithdraw(WithdrawProcessDTO dto);
    
    /**
     * 删除提现订单
     */
    Response deleteWithdraw(Long id);
}

