package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.FindVipOrderListDTO;
import com.zm.zx.admin.service.VipOrderService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * VIP订单管理 Controller
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/vip-order")
public class VipOrderController {
    
    private final VipOrderService vipOrderService;
    
    /**
     * 获取VIP订单列表（分页）
     */
    @ApiOperationLog(description = "获取VIP订单列表")
    @PostMapping("/list")
    public PageResponse list(@RequestBody FindVipOrderListDTO findVipOrderListDTO) {
        return vipOrderService.findVipOrderList(findVipOrderListDTO);
    }
    
    /**
     * 根据ID获取VIP订单详情
     */
    @ApiOperationLog(description = "获取VIP订单详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return vipOrderService.getVipOrderById(id);
    }
    
    /**
     * 删除VIP订单
     */
    @ApiOperationLog(description = "删除VIP订单")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long id) {
        return vipOrderService.deleteVipOrder(id);
    }
}

