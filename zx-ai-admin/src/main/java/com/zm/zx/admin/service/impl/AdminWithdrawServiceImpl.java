package com.zm.zx.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.admin.mapper.AdminWithdrawOrderMapper;
import com.zm.zx.admin.model.dto.FindWithdrawListDTO;
import com.zm.zx.admin.model.dto.WithdrawProcessDTO;
import com.zm.zx.admin.service.AdminWithdrawService;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.model.withdraw.po.WithdrawOrder;
import com.zm.zx.web.helper.WalletRefundHelper;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.pay.service.AlipayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 提现管理服务实现类（后台）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWithdrawServiceImpl implements AdminWithdrawService {
    
    private final AdminWithdrawOrderMapper withdrawOrderMapper;
    private final WalletRefundHelper walletRefundHelper;
    private final AlipayService alipayService;
    
    @Override
    public PageResponse findWithdrawList(FindWithdrawListDTO dto) {
        // 分页查询（关联用户表）
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        Page<com.zm.zx.admin.model.vo.WithdrawOrderVO> page = new Page<>(pageNum, pageSize);
        
        // 调用Mapper的自定义方法，关联查询用户表
        com.baomidou.mybatisplus.core.metadata.IPage<com.zm.zx.admin.model.vo.WithdrawOrderVO> result = 
            withdrawOrderMapper.selectWithdrawListWithUser(
                page,
                dto.getWithdrawNo(),
                dto.getUserId(),
                dto.getStatus()
            );
        
        return PageResponse.success(result.getRecords(), pageNum, result.getTotal(), pageSize);
    }
    
    @Override
    public Response getWithdrawById(Long id) {
        // 查询提现订单详情（关联用户表）
        com.zm.zx.admin.model.vo.WithdrawOrderVO withdrawOrderVO = withdrawOrderMapper.selectWithdrawByIdWithUser(id);
        if (withdrawOrderVO == null) {
            throw new BizException("提现订单不存在");
        }
        return Response.success(withdrawOrderVO);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response processWithdraw(WithdrawProcessDTO dto) {
        // 获取当前管理员ID
        Long adminId = StpUtil.getLoginIdAsLong();
        
        // 查询提现订单
        WithdrawOrder withdrawOrder = withdrawOrderMapper.selectById(dto.getId());
        if (withdrawOrder == null) {
            throw new BizException("提现订单不存在");
        }
        
        // 只能处理待处理状态的订单
        if (withdrawOrder.getStatus() != 0) {
            throw new BizException("该提现订单已处理，无法重复处理");
        }
        
        // 验证处理状态
        if (dto.getStatus() != 1 && dto.getStatus() != 2) {
            throw new BizException("处理状态不正确");
        }
        
        // 如果是拒绝，必须填写拒绝原因
        if (dto.getStatus() == 2 && StrUtil.isBlank(dto.getRejectReason())) {
            throw new BizException("拒绝时必须填写拒绝原因");
        }
        
        if (dto.getStatus() == 2) {
            // 拒绝：需要退回钱包余额并立即更新订单状态
            withdrawOrder.setStatus(2);
            withdrawOrder.setProcessTime(LocalDateTime.now());
            withdrawOrder.setProcessAdminId(adminId);
            withdrawOrder.setRejectReason(dto.getRejectReason());
            
            // 使用公共服务执行钱包退款
            walletRefundHelper.refundForWithdrawReject(
                withdrawOrder.getUserId(),
                withdrawOrder.getAmount(),
                withdrawOrder.getWithdrawNo()
            );
            
            log.info("提现申请已拒绝，退回金额到钱包，withdrawNo: {}, userId: {}, amount: {}",
                    withdrawOrder.getWithdrawNo(), withdrawOrder.getUserId(), withdrawOrder.getAmount());
            
            // 拒绝时保存备注并更新订单
            if (StrUtil.isNotBlank(dto.getRemark())) {
                withdrawOrder.setRemark(dto.getRemark());
            }
            withdrawOrderMapper.updateById(withdrawOrder);
            
            return Response.success();
        } else {
            // 通过：直接调用支付宝转账接口转账到用户账号
            log.info("开始发起提现转账，withdrawNo: {}, accountInfo: {}, actualAmount: {}",
                    withdrawOrder.getWithdrawNo(), withdrawOrder.getAccountInfo(), withdrawOrder.getActualAmount());
            
            try {
                // 调用支付宝单笔转账接口，直接转账到用户账号
                Response transferResult = alipayService.transferToUser(
                        withdrawOrder.getWithdrawNo(),
                        withdrawOrder.getAccountInfo(), // 收款方账号
                        withdrawOrder.getActualAmount(), // 实际到账金额
                        withdrawOrder.getAccountName(), // 收款方姓名
                        "提现转账 - " + withdrawOrder.getWithdrawNo()
                );
                
                if (transferResult.isSuccess()) {
                    // 转账成功，获取转账信息
                    Map<String, Object> transferData = (Map<String, Object>) transferResult.getData();
                    String alipayOrderId = (String) transferData.get("orderId"); // 支付宝转账单据号
                    
                    log.info("提现转账成功，withdrawNo: {}, alipayOrderId: {}, 收款账号: {}, 收款人: {}", 
                            withdrawOrder.getWithdrawNo(), alipayOrderId, 
                            withdrawOrder.getAccountInfo(), withdrawOrder.getAccountName());
                    
                    // 转账成功，立即更新订单状态为已完成
                    withdrawOrder.setStatus(1);
                    withdrawOrder.setProcessTime(LocalDateTime.now());
                    withdrawOrder.setProcessAdminId(adminId);
                    withdrawOrder.setAlipayOrderId(alipayOrderId); // 保存支付宝转账单号
                    
                    if (StrUtil.isNotBlank(dto.getRemark())) {
                        withdrawOrder.setRemark(dto.getRemark());
                    }
                    
                    withdrawOrderMapper.updateById(withdrawOrder);
                    
                    // 返回转账成功信息
                    return Response.success(transferData);
                } else {
                    log.error("转账失败，withdrawNo: {}, msg: {}", withdrawOrder.getWithdrawNo(), transferResult.getMessage());
                    throw new BizException("转账失败：" + transferResult.getMessage());
                }
            } catch (Exception e) {
                log.error("转账异常，withdrawNo: {}", withdrawOrder.getWithdrawNo(), e);
                throw new BizException("转账失败：" + e.getMessage());
            }
        }
    }
    
    @Override
    public Response deleteWithdraw(Long id) {
        WithdrawOrder withdrawOrder = withdrawOrderMapper.selectById(id);
        if (withdrawOrder == null) {
            throw new BizException("提现订单不存在");
        }
        
        // 只能删除已拒绝的订单
        if (withdrawOrder.getStatus() != 2) {
            throw new BizException("只能删除已拒绝的提现订单");
        }
        
        withdrawOrderMapper.deleteById(id);
        log.info("提现订单已删除，id: {}", id);
        
        return Response.success();
    }
}


