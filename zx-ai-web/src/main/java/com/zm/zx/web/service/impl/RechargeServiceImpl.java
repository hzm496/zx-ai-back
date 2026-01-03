package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RechargeCreateDTO;
import com.zm.zx.web.domain.po.RechargeOrder;
import com.zm.zx.web.domain.po.UserWallet;
import com.zm.zx.web.domain.po.WalletTransaction;
import com.zm.zx.web.domain.vo.RechargeOrderVO;
import com.zm.zx.web.mapper.RechargeOrderMapper;
import com.zm.zx.web.mapper.UserWalletMapper;
import com.zm.zx.web.mapper.WalletTransactionMapper;
import com.zm.zx.web.service.RechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 充值服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeServiceImpl implements RechargeService {
    
    private final RechargeOrderMapper rechargeOrderMapper;
    private final UserWalletMapper walletMapper;
    private final WalletTransactionMapper transactionMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createRechargeOrder(RechargeCreateDTO dto) {
        log.info("创建充值订单，金额: {}", dto.getAmount());
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 检查钱包是否开通
        LambdaQueryWrapper<UserWallet> walletQueryWrapper = new LambdaQueryWrapper<>();
        walletQueryWrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = walletMapper.selectOne(walletQueryWrapper);
        
        if (wallet == null || wallet.getIsActivated() == 0) {
            throw new BizException("请先开通钱包");
        }
        
        // 生成订单号（格式：RECHARGE + 时间戳 + 用户ID后4位）
        String orderNo = generateOrderNo(userId);
        
        // 创建充值订单
        RechargeOrder order = RechargeOrder.builder()
                .orderNo(orderNo)
                .userId(userId)
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(0) // 待支付
                .build();
        
        rechargeOrderMapper.insert(order);
        
        log.info("充值订单创建成功，orderNo: {}, userId: {}, amount: {}", orderNo, userId, dto.getAmount());
        
        // 返回订单信息
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("amount", dto.getAmount());
        result.put("paymentMethod", dto.getPaymentMethod());
        
        return Response.success(result);
    }
    
    @Override
    public Response getRechargeOrderList(Integer pageNo, Integer pageSize) {
        log.info("获取充值订单列表，pageNo: {}, pageSize: {}", pageNo, pageSize);
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 分页查询充值订单
        Page<RechargeOrder> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<RechargeOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeOrder::getUserId, userId)
                .orderByDesc(RechargeOrder::getCreateTime);
        
        Page<RechargeOrder> orderPage = rechargeOrderMapper.selectPage(page, queryWrapper);
        
        // 状态名称映射
        Map<Integer, String> statusNameMap = new HashMap<>();
        statusNameMap.put(0, "待支付");
        statusNameMap.put(1, "已支付");
        statusNameMap.put(2, "已取消");
        statusNameMap.put(3, "已退款");
        
        // 支付方式名称映射
        Map<Integer, String> paymentMethodNameMap = new HashMap<>();
        paymentMethodNameMap.put(1, "支付宝");
        
        // 转换为VO
        List<RechargeOrderVO> orderVOList = orderPage.getRecords().stream()
                .map(order -> RechargeOrderVO.builder()
                        .id(order.getId())
                        .orderNo(order.getOrderNo())
                        .userId(order.getUserId())
                        .amount(order.getAmount())
                        .paymentMethod(order.getPaymentMethod())
                        .paymentMethodName(paymentMethodNameMap.getOrDefault(order.getPaymentMethod(), "未知"))
                        .status(order.getStatus())
                        .statusName(statusNameMap.getOrDefault(order.getStatus(), "未知"))
                        .alipayTradeNo(order.getAlipayTradeNo())
                        .payTime(order.getPayTime())
                        .createTime(order.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        
        log.info("获取充值订单列表成功，共{}条", orderVOList.size());
        return PageResponse.success(orderVOList, pageNo, orderPage.getTotal(), pageSize);
    }
    
    @Override
    public Response getRechargeOrderByOrderNo(String orderNo) {
        log.info("获取充值订单详情，orderNo: {}", orderNo);
        
        // 查询订单
        LambdaQueryWrapper<RechargeOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeOrder::getOrderNo, orderNo);
        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BizException("订单不存在");
        }
        
        // 验证订单归属（防止越权访问）
        if (StpUtil.isLogin()) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!order.getUserId().equals(currentUserId)) {
                throw new BizException("无权访问该订单");
            }
        }
        
        // 状态名称映射
        Map<Integer, String> statusNameMap = new HashMap<>();
        statusNameMap.put(0, "待支付");
        statusNameMap.put(1, "已支付");
        statusNameMap.put(2, "已取消");
        statusNameMap.put(3, "已退款");
        
        // 支付方式名称映射
        Map<Integer, String> paymentMethodNameMap = new HashMap<>();
        paymentMethodNameMap.put(1, "支付宝");
        
        // 转换为VO
        RechargeOrderVO orderVO = RechargeOrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .amount(order.getAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentMethodName(paymentMethodNameMap.getOrDefault(order.getPaymentMethod(), "未知"))
                .status(order.getStatus())
                .statusName(statusNameMap.getOrDefault(order.getStatus(), "未知"))
                .alipayTradeNo(order.getAlipayTradeNo())
                .payTime(order.getPayTime())
                .createTime(order.getCreateTime())
                .build();
        
        log.info("获取充值订单详情成功");
        return Response.success(orderVO);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response handleRechargeSuccess(String orderNo, String tradeNo) {
        log.info("处理充值成功回调，orderNo: {}, tradeNo: {}", orderNo, tradeNo);
        
        // 查询充值订单
        LambdaQueryWrapper<RechargeOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeOrder::getOrderNo, orderNo);
        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            log.error("充值订单不存在，orderNo: {}", orderNo);
            return Response.fail("充值订单不存在");
        }
        
        // 判断订单状态，避免重复处理
        if (order.getStatus() == 1) {
            log.info("充值订单已处理，orderNo: {}", orderNo);
            return Response.success("充值订单已处理");
        }
        
        // 更新订单状态
        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        order.setAlipayTradeNo(tradeNo);
        rechargeOrderMapper.updateById(order);
        
        // 查询用户钱包
        LambdaQueryWrapper<UserWallet> walletQueryWrapper = new LambdaQueryWrapper<>();
        walletQueryWrapper.eq(UserWallet::getUserId, order.getUserId());
        UserWallet wallet = walletMapper.selectOne(walletQueryWrapper);
        
        if (wallet == null) {
            log.error("用户钱包不存在，userId: {}", order.getUserId());
            return Response.fail("用户钱包不存在");
        }
        
        // 记录充值前余额
        BigDecimal balanceBefore = wallet.getBalance();
        
        // 增加钱包余额
        BigDecimal newBalance = balanceBefore.add(order.getAmount());
        wallet.setBalance(newBalance);
        walletMapper.updateById(wallet);
        
        // 生成交易流水号
        String transactionNo = generateTransactionNo();
        
        // 记录交易流水
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(order.getUserId())
                .transactionNo(transactionNo)
                .type(1) // 充值
                .amount(order.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(newBalance)
                .businessType("RECHARGE")
                .businessId(order.getId())
                .remark("支付宝充值")
                .build();
        transactionMapper.insert(transaction);
        
        log.info("充值成功处理完成，orderNo: {}, userId: {}, amount: {}, newBalance: {}", 
                orderNo, order.getUserId(), order.getAmount(), newBalance);
        
        return Response.success("充值成功");
    }
    
    /**
     * 生成订单号
     * 格式：RECHARGE + 时间戳（yyyyMMddHHmmss） + 用户ID后4位
     */
    private String generateOrderNo(Long userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdSuffix = String.format("%04d", userId % 10000);
        return "RECHARGE" + timestamp + userIdSuffix;
    }
    
    /**
     * 生成交易流水号
     * 格式：TXN + 时间戳（yyyyMMddHHmmssSSS） + 随机4位数
     */
    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = (int) (Math.random() * 10000);
        return "TXN" + timestamp + String.format("%04d", random);
    }
}

