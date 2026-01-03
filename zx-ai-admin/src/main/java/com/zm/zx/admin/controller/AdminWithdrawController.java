package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.FindWithdrawListDTO;
import com.zm.zx.admin.model.dto.WithdrawProcessDTO;
import com.zm.zx.admin.service.AdminWithdrawService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 提现管理Controller（后台）
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/withdraw")
public class AdminWithdrawController {
    
    private final AdminWithdrawService withdrawService;
    
    /**
     * 获取提现订单列表（分页）
     */
    @ApiOperationLog(description = "获取提现订单列表")
    @PostMapping("/list")
    public PageResponse list(@RequestBody FindWithdrawListDTO findWithdrawListDTO) {
        return withdrawService.findWithdrawList(findWithdrawListDTO);
    }
    
    /**
     * 根据ID获取提现订单详情
     */
    @ApiOperationLog(description = "获取提现订单详情")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable("id") Long id) {
        return withdrawService.getWithdrawById(id);
    }
    
    /**
     * 处理提现申请（审核）
     */
    @ApiOperationLog(description = "处理提现申请")
    @PostMapping("/process")
    public Response process(@Validated @RequestBody WithdrawProcessDTO dto) {
        return withdrawService.processWithdraw(dto);
    }
    
    /**
     * 删除提现订单
     */
    @ApiOperationLog(description = "删除提现订单")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long id) {
        return withdrawService.deleteWithdraw(id);
    }
}

