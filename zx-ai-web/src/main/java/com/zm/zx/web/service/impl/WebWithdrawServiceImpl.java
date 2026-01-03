package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.WithdrawCreateDTO;
import com.zm.zx.web.helper.WalletRefundHelper;
import com.zm.zx.web.domain.po.UserWallet;
import com.zm.zx.web.domain.po.WalletTransaction;
import com.zm.zx.common.model.withdraw.po.WithdrawOrder;
import com.zm.zx.web.domain.vo.WithdrawOrderVO;
import com.zm.zx.web.mapper.UserWalletMapper;
import com.zm.zx.web.mapper.WalletTransactionMapper;
import com.zm.zx.web.mapper.WebWithdrawOrderMapper;
import com.zm.zx.web.service.WebWithdrawService;
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
 * 提现服务实现类（前台用户端）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebWithdrawServiceImpl implements WebWithdrawService {
    
    private final WebWithdrawOrderMapper withdrawOrderMapper;
    private final UserWalletMapper walletMapper;
    private final WalletTransactionMapper transactionMapper;
    private final WalletRefundHelper walletRefundHelper;
    
    // 提现手续费率（1%）
    private static final BigDecimal FEE_RATE = new BigDecimal("0.01");
    // 最低提现金额
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1.00");
    // 单笔最高提现金额
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createWithdrawOrder(WithdrawCreateDTO dto) {
        log.info("创建提现申请，金额: {}", dto.getAmount());
        
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
        
        if (wallet.getStatus() != 1) {
            throw new BizException("钱包状态异常，无法提现");
        }
        
        // 检查是否绑定支付宝账号
        if (dto.getAccountType() == 1) { // 支付宝提现
            if (wallet.getAlipayAccount() == null || wallet.getAlipayAccount().isEmpty()) {
                throw new BizException("请先在钱包中绑定支付宝账号");
            }
            if (wallet.getAlipayName() == null || wallet.getAlipayName().isEmpty()) {
                throw new BizException("请先在钱包中绑定支付宝账户姓名");
            }
        }
        
        // 验证提现金额
        if (dto.getAmount().compareTo(MIN_AMOUNT) < 0) {
            throw new BizException("提现金额不能少于" + MIN_AMOUNT + "元");
        }
        
        if (dto.getAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new BizException("单笔提现金额不能超过" + MAX_AMOUNT + "元");
        }
        
        // 检查钱包余额
        if (wallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BizException("钱包余额不足");
        }
        
        // 计算手续费和实际到账金额
        BigDecimal fee = dto.getAmount().multiply(FEE_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal actualAmount = dto.getAmount().subtract(fee);
        
        // 生成提现流水号（格式：WITHDRAW + 时间戳 + 用户ID后4位）
        String withdrawNo = generateWithdrawNo(userId);
        
        // 创建提现订单（使用钱包绑定的支付宝账号）
        WithdrawOrder order = WithdrawOrder.builder()
                .userId(userId)
                .withdrawNo(withdrawNo)
                .amount(dto.getAmount())
                .actualAmount(actualAmount)
                .fee(fee)
                .status(0) // 待处理
                .accountType(dto.getAccountType())
                .accountInfo(wallet.getAlipayAccount())  // 使用钱包绑定的支付宝账号
                .accountName(wallet.getAlipayName())     // 使用钱包绑定的支付宝姓名
                .remark(dto.getRemark())
                .build();
        
        withdrawOrderMapper.insert(order);
        
        // 冻结钱包余额（从余额中扣除提现金额）
        wallet.setBalance(wallet.getBalance().subtract(dto.getAmount()));
        walletMapper.updateById(wallet);
        
        // 记录钱包交易（提现冻结）
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(userId)
                .transactionNo(withdrawNo)
                .type(4) // 4-提现（新增一个类型）
                .amount(dto.getAmount())
                .balanceBefore(wallet.getBalance().add(dto.getAmount()))
                .balanceAfter(wallet.getBalance())
                .remark("提现申请 - " + actualAmount + "元（手续费" + fee + "元）")
                .build();
        
        transactionMapper.insert(transaction);
        
        log.info("提现订单创建成功，withdrawNo: {}, userId: {}, amount: {}, actualAmount: {}, fee: {}",
                withdrawNo, userId, dto.getAmount(), actualAmount, fee);
        
        // 返回订单信息
        Map<String, Object> result = new HashMap<>();
        result.put("withdrawNo", withdrawNo);
        result.put("amount", dto.getAmount());
        result.put("actualAmount", actualAmount);
        result.put("fee", fee);
        
        return Response.success(result);
    }
    
    @Override
    public Response getWithdrawOrderList(Integer pageNo, Integer pageSize, Integer status) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 分页查询
        Page<WithdrawOrder> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<WithdrawOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawOrder::getUserId, userId);
        
        // 状态筛选
        if (status != null) {
            queryWrapper.eq(WithdrawOrder::getStatus, status);
        }
        
        queryWrapper.orderByDesc(WithdrawOrder::getCreateTime);
        
        Page<WithdrawOrder> result = withdrawOrderMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        List<WithdrawOrderVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageResponse.success(voList, pageNo,result.getTotal(),pageSize);
    }
    
    @Override
    public Response getWithdrawOrderById(Long id) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询提现订单
        WithdrawOrder order = withdrawOrderMapper.selectById(id);
        
        if (order == null) {
            throw new BizException("提现订单不存在");
        }
        
        // 检查是否是本人的订单
        if (!order.getUserId().equals(userId)) {
            throw new BizException("无权访问该订单");
        }
        
        return Response.success(convertToVO(order));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response cancelWithdrawOrder(Long id) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询提现订单
        WithdrawOrder order = withdrawOrderMapper.selectById(id);
        
        if (order == null) {
            throw new BizException("提现订单不存在");
        }
        
        // 检查是否是本人的订单
        if (!order.getUserId().equals(userId)) {
            throw new BizException("无权操作该订单");
        }
        
        // 只有待处理状态的订单可以取消
        if (order.getStatus() != 0) {
            throw new BizException("该订单不能取消");
        }
        
        // 更新订单状态为已拒绝（用2表示用户主动取消）
        order.setStatus(2);
        order.setRejectReason("用户主动取消");
        withdrawOrderMapper.updateById(order);
        
        // 使用公共服务执行钱包退款
        walletRefundHelper.refundForWithdrawCancel(
            userId,
            order.getAmount(),
            order.getWithdrawNo()
        );
        
        log.info("提现订单取消成功，withdrawNo: {}, userId: {}, amount: {}",
                order.getWithdrawNo(), userId, order.getAmount());
        
        return Response.success();
    }
    
    /**
     * 生成提现流水号
     */
    private String generateWithdrawNo(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdSuffix = String.format("%04d", userId % 10000);
        return "WITHDRAW" + timestamp + userIdSuffix;
    }
    
    /**
     * 转换为VO对象（脱敏处理）
     */
    private WithdrawOrderVO convertToVO(WithdrawOrder order) {
        return WithdrawOrderVO.builder()
                .id(order.getId())
                .withdrawNo(order.getWithdrawNo())
                .amount(order.getAmount())
                .actualAmount(order.getActualAmount())
                .fee(order.getFee())
                .status(order.getStatus())
                .statusName(getStatusName(order.getStatus()))
                .accountType(order.getAccountType())
                .accountTypeName(getAccountTypeName(order.getAccountType()))
                .accountInfo(maskAccountInfo(order.getAccountInfo(), order.getAccountType()))
                .accountName(order.getAccountName())
                .rejectReason(order.getRejectReason())
                .processTime(order.getProcessTime())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .build();
    }
    
    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        switch (status) {
            case 0:
                return "待处理";
            case 1:
                return "已完成";
            case 2:
                return "已拒绝";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取账户类型名称
     */
    private String getAccountTypeName(Integer accountType) {
        switch (accountType) {
            case 1:
                return "支付宝";
            case 2:
                return "微信";
            case 3:
                return "银行卡";
            default:
                return "未知";
        }
    }
    
    /**
     * 脱敏处理账户信息
     */
    private String maskAccountInfo(String accountInfo, Integer accountType) {
        if (accountInfo == null || accountInfo.length() < 4) {
            return accountInfo;
        }
        
        int length = accountInfo.length();
        if (accountType == 3) { // 银行卡：显示前4位和后4位
            if (length > 8) {
                return accountInfo.substring(0, 4) + "****" + accountInfo.substring(length - 4);
            }
        } else { // 支付宝/微信：显示前3位和后4位
            if (length > 7) {
                return accountInfo.substring(0, 3) + "****" + accountInfo.substring(length - 4);
            }
        }
        
        return accountInfo.substring(0, 3) + "****";
    }
}

