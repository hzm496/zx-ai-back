package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RechargeCreateDTO;
import com.zm.zx.web.service.RechargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 充值控制器
 */
@Slf4j
@RestController
@RequestMapping("/web/recharge")
@RequiredArgsConstructor
public class RechargeController {
    
    private final RechargeService rechargeService;
    
    /**
     * 创建充值订单
     */
    @ApiOperationLog(description = "创建充值订单")
    @SaCheckLogin
    @PostMapping("/create")
    public Response createRechargeOrder(@Valid @RequestBody RechargeCreateDTO dto) {
        log.info("创建充值订单，金额: {}", dto.getAmount());
        return rechargeService.createRechargeOrder(dto);
    }
    
    /**
     * 获取充值订单列表（分页）
     */
    @ApiOperationLog(description = "获取充值订单列表")
    @SaCheckLogin
    @GetMapping("/list")
    public Response getRechargeOrderList(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("获取充值订单列表，pageNo: {}, pageSize: {}", pageNo, pageSize);
        return rechargeService.getRechargeOrderList(pageNo, pageSize);
    }
    
    /**
     * 根据订单号获取充值订单详情
     */
    @ApiOperationLog(description = "获取充值订单详情")
    @SaCheckLogin
    @GetMapping("/detail/{orderNo}")
    public Response getRechargeOrderByOrderNo(@PathVariable String orderNo) {
        log.info("获取充值订单详情，orderNo: {}", orderNo);
        return rechargeService.getRechargeOrderByOrderNo(orderNo);
    }
}

