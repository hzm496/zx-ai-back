package com.zm.zx.web.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.web.domain.po.UserWallet;
import com.zm.zx.web.domain.po.WalletTransaction;
import com.zm.zx.web.mapper.UserWalletMapper;
import com.zm.zx.web.mapper.WalletTransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 钱包退款助手类
 * 统一处理钱包退款逻辑，避免代码重复
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WalletRefundHelper {
    
    private final UserWalletMapper userWalletMapper;
    private final WalletTransactionMapper walletTransactionMapper;
    
    /**
     * 执行钱包退款
     * 
     * @param userId 用户ID
     * @param amount 退款金额
     * @param transactionNo 交易流水号（如：WITHDRAW20250101120000_REJECT）
     * @param remark 退款备注
     * @return 退款后的钱包余额
     */
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal executeRefund(Long userId, BigDecimal amount, String transactionNo, String remark) {
        log.info("开始执行钱包退款，userId: {}, amount: {}, transactionNo: {}", userId, amount, transactionNo);
        
        // 1. 查询用户钱包
        LambdaQueryWrapper<UserWallet> walletQueryWrapper = new LambdaQueryWrapper<>();
        walletQueryWrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = userWalletMapper.selectOne(walletQueryWrapper);
        
        if (wallet == null) {
            throw new BizException("用户钱包不存在");
        }
        
        // 2. 记录退款前余额
        BigDecimal balanceBefore = wallet.getBalance();
        
        // 3. 增加钱包余额
        BigDecimal balanceAfter = balanceBefore.add(amount);
        wallet.setBalance(balanceAfter);
        userWalletMapper.updateById(wallet);
        
        // 4. 记录钱包交易流水
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(userId)
                .transactionNo(transactionNo)
                .type(3) // 3-退款
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .remark(remark)
                .build();
        
        walletTransactionMapper.insert(transaction);
        
        log.info("钱包退款完成，userId: {}, amount: {}, balanceBefore: {}, balanceAfter: {}", 
                userId, amount, balanceBefore, balanceAfter);
        
        return balanceAfter;
    }
    
    /**
     * 提现拒绝退款（快捷方法）
     * 
     * @param userId 用户ID
     * @param amount 退款金额
     * @param withdrawNo 提现订单号
     * @return 退款后的钱包余额
     */
    public BigDecimal refundForWithdrawReject(Long userId, BigDecimal amount, String withdrawNo) {
        String transactionNo = withdrawNo + "_REJECT";
        String remark = "提现拒绝 - 退回" + amount + "元";
        return executeRefund(userId, amount, transactionNo, remark);
    }
    
    /**
     * 提现取消退款（快捷方法）
     * 
     * @param userId 用户ID
     * @param amount 退款金额
     * @param withdrawNo 提现订单号
     * @return 退款后的钱包余额
     */
    public BigDecimal refundForWithdrawCancel(Long userId, BigDecimal amount, String withdrawNo) {
        String transactionNo = withdrawNo + "_CANCEL";
        String remark = "提现取消 - 退回" + amount + "元";
        return executeRefund(userId, amount, transactionNo, remark);
    }
}
