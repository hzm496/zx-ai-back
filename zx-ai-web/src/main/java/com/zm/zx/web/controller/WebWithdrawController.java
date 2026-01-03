package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.WithdrawCreateDTO;
import com.zm.zx.web.service.WebWithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 提现控制器（前台用户端）
 */
@Slf4j
@RestController
@RequestMapping("/web/withdraw")
@RequiredArgsConstructor
public class WebWithdrawController {
    
    private final WebWithdrawService withdrawService;
    
    /**
     * 创建提现申请
     */
    @ApiOperationLog(description = "创建提现申请")
    @PostMapping("/create")
    public Response createWithdrawOrder(@Validated @RequestBody WithdrawCreateDTO dto) {
        return withdrawService.createWithdrawOrder(dto);
    }
    
    /**
     * 获取提现订单列表（分页）
     */
    @ApiOperationLog(description = "获取提现订单列表")
    @GetMapping("/list")
    public Response getWithdrawOrderList(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        return withdrawService.getWithdrawOrderList(pageNo, pageSize, status);
    }
    
    /**
     * 根据ID获取提现订单详情
     */
    @ApiOperationLog(description = "获取提现订单详情")
    @GetMapping("/detail/{id}")
    public Response getWithdrawOrderById(@PathVariable("id") Long id) {
        return withdrawService.getWithdrawOrderById(id);
    }
    
    /**
     * 取消提现申请
     */
    @ApiOperationLog(description = "取消提现申请")
    @PostMapping("/cancel/{id}")
    public Response cancelWithdrawOrder(@PathVariable("id") Long id) {
        return withdrawService.cancelWithdrawOrder(id);
    }
}

