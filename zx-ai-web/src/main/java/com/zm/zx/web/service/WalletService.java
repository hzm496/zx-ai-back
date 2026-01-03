package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.WalletActivateDTO;

/**
 * 钱包 Service
 */
public interface WalletService {
    
    /**
     * 获取当前用户的钱包信息
     * 
     * @return 钱包信息
     */
    Response getWalletInfo();
    
    /**
     * 开通钱包（设置支付密码）
     * 
     * @param dto 开通钱包DTO
     * @return 开通结果
     */
    Response activateWallet(WalletActivateDTO dto);
    
    /**
     * 修改支付密码
     * 
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    Response changePaymentPassword(String oldPassword, String newPassword);
    
    /**
     * 验证支付密码
     * 
     * @param password 支付密码
     * @return 是否正确
     */
    boolean verifyPaymentPassword(String password);
    
    /**
     * 获取交易记录（分页）
     * 
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 交易记录列表
     */
    Response getTransactions(Integer pageNo, Integer pageSize);
}

