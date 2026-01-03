package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.WalletActivateDTO;
import com.zm.zx.web.domain.po.UserWallet;
import com.zm.zx.web.domain.po.WalletTransaction;
import com.zm.zx.web.domain.vo.WalletTransactionVO;
import com.zm.zx.web.domain.vo.WalletVO;
import com.zm.zx.web.mapper.UserWalletMapper;
import com.zm.zx.web.mapper.WalletTransactionMapper;
import com.zm.zx.web.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 钱包 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    
    private final UserWalletMapper walletMapper;
    private final WalletTransactionMapper transactionMapper;
    private final PasswordEncoder encoder;
    
    @Override
    public Response getWalletInfo() {
        log.info("获取钱包信息");
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询钱包信息
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = walletMapper.selectOne(queryWrapper);
        
        // 如果钱包不存在，返回未开通状态
        if (wallet == null) {
            WalletVO walletVO = WalletVO.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .isActivated(0)
                    .status(1)
                    .hasPaymentPassword(false)
                    .build();
            return Response.success(walletVO);
        }
        
        // 转换为VO
        WalletVO walletVO = WalletVO.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .isActivated(wallet.getIsActivated())
                .status(wallet.getStatus())
                .hasPaymentPassword(wallet.getPaymentPassword() != null && !wallet.getPaymentPassword().isEmpty())
                .alipayAccount(wallet.getAlipayAccount())
                .alipayName(wallet.getAlipayName())
                .createTime(wallet.getCreateTime())
                .build();
        
        log.info("获取钱包信息成功");
        return Response.success(walletVO);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response activateWallet(WalletActivateDTO dto) {
        log.info("开通钱包");
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证两次密码是否一致
        if (!dto.getPaymentPassword().equals(dto.getConfirmPassword())) {
            throw new BizException("两次密码输入不一致");
        }
        
        // 检查钱包是否已存在
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, userId);
        UserWallet existWallet = walletMapper.selectOne(queryWrapper);
        
        if (existWallet != null && existWallet.getIsActivated() == 1) {
            throw new BizException("钱包已开通，无需重复开通");
        }
        
        // 加密支付密码
        String encryptedPassword = encoder.encode(dto.getPaymentPassword());
        
        if (existWallet != null) {
            // 更新现有记录
            existWallet.setPaymentPassword(encryptedPassword);
            existWallet.setAlipayAccount(dto.getAlipayAccount());
            existWallet.setAlipayName(dto.getAlipayName());
            existWallet.setIsActivated(1);
            existWallet.setStatus(1);
            walletMapper.updateById(existWallet);
        } else {
            // 创建新钱包
            UserWallet wallet = UserWallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .paymentPassword(encryptedPassword)
                    .alipayAccount(dto.getAlipayAccount())
                    .alipayName(dto.getAlipayName())
                    .isActivated(1)
                    .status(1)
                    .build();
            walletMapper.insert(wallet);
        }
        
        log.info("钱包开通成功，userId: {}", userId);
        return Response.success("钱包开通成功");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response changePaymentPassword(String oldPassword, String newPassword) {
        log.info("修改支付密码");
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询钱包
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = walletMapper.selectOne(queryWrapper);
        
        if (wallet == null || wallet.getIsActivated() == 0) {
            throw new BizException("钱包未开通");
        }
        
        // 验证旧密码
        if (!encoder.matches(oldPassword, wallet.getPaymentPassword())) {
            throw new BizException("原密码错误");
        }
        
        // 加密新密码并更新
        String encryptedPassword = encoder.encode(newPassword);
        wallet.setPaymentPassword(encryptedPassword);
        walletMapper.updateById(wallet);
        
        log.info("支付密码修改成功");
        return Response.success("支付密码修改成功");
    }
    
    @Override
    public boolean verifyPaymentPassword(String password) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            return false;
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询钱包
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = walletMapper.selectOne(queryWrapper);
        
        if (wallet == null || wallet.getPaymentPassword() == null) {
            return false;
        }
        
        return encoder.matches(password, wallet.getPaymentPassword());
    }
    
    @Override
    public Response getTransactions(Integer pageNo, Integer pageSize) {
        log.info("获取交易记录，pageNo: {}, pageSize: {}", pageNo, pageSize);
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 分页查询交易记录
        Page<WalletTransaction> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<WalletTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WalletTransaction::getUserId, userId)
                .orderByDesc(WalletTransaction::getCreateTime);
        
        Page<WalletTransaction> transactionPage = transactionMapper.selectPage(page, queryWrapper);
        
        // 交易类型名称映射
        Map<Integer, String> typeNameMap = new HashMap<>();
        typeNameMap.put(1, "充值");
        typeNameMap.put(2, "消费");
        typeNameMap.put(3, "退款");
        typeNameMap.put(4, "提现");
        
        // 转换为VO
        List<WalletTransactionVO> transactionVOList = transactionPage.getRecords().stream()
                .map(transaction -> WalletTransactionVO.builder()
                        .id(transaction.getId())
                        .transactionNo(transaction.getTransactionNo())
                        .type(transaction.getType())
                        .typeName(typeNameMap.getOrDefault(transaction.getType(), "未知"))
                        .amount(transaction.getAmount())
                        .balanceBefore(transaction.getBalanceBefore())
                        .balanceAfter(transaction.getBalanceAfter())
                        .businessType(transaction.getBusinessType())
                        .remark(transaction.getRemark())
                        .createTime(transaction.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        
        log.info("获取交易记录成功，共{}条", transactionVOList.size());
        return PageResponse.success(transactionVOList, pageNo, transactionPage.getTotal(), pageSize);
    }
}

