package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.VipOrderCreateDTO;
import com.zm.zx.web.domain.dto.VipPaymentDTO;
import com.zm.zx.web.service.VipService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Web端 - VIP Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/vip")
public class VipController {
    
    private final VipService vipService;
    
    /**
     * 获取VIP套餐列表
     */
    @ApiOperationLog(description = "获取VIP套餐列表")
    @GetMapping("/packages")
    public Response getPackages() {
        return vipService.getPackages();
    }
    
    /**
     * 创建VIP订单
     */
    @SaCheckLogin
    @ApiOperationLog(description = "创建VIP订单")
    @PostMapping("/order/create")
    public Response createOrder(@Validated @RequestBody VipOrderCreateDTO dto) {
        return vipService.createOrder(dto);
    }
    
    /**
     * 支付VIP订单
     */
    @SaCheckLogin
    @ApiOperationLog(description = "支付VIP订单")
    @PostMapping("/order/pay")
    public Response payOrder(@Validated @RequestBody VipPaymentDTO dto) {
        return vipService.payOrder(dto);
    }
    
    /**
     * 获取订单详情
     */
    @SaCheckLogin
    @ApiOperationLog(description = "获取VIP订单详情")
    @GetMapping("/order/{orderNo}")
    public Response getOrderDetail(@PathVariable String orderNo) {
        return vipService.getOrderDetail(orderNo);
    }
    
    /**
     * 获取当前用户的VIP状态
     */
    @SaCheckLogin
    @ApiOperationLog(description = "获取VIP状态")
    @GetMapping("/status")
    public Response getVipStatus() {
        return vipService.getVipStatus();
    }
    
    /**
     * 获取当前用户的VIP订单列表
     */
    @SaCheckLogin
    @ApiOperationLog(description = "获取VIP订单列表")
    @GetMapping("/orders")
    public Response getVipOrders(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return vipService.getVipOrders(pageNo, pageSize);
    }
}

