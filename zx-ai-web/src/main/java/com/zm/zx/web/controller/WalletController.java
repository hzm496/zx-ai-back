package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.WalletActivateDTO;
import com.zm.zx.web.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 钱包控制器
 */
@RestController
@RequestMapping("/web/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {
    
    private final WalletService walletService;
    
    /**
     * 获取钱包信息
     */
    @ApiOperationLog(description = "获取钱包信息")
    @SaCheckLogin
    @GetMapping("/info")
    public Response getWalletInfo() {
        return walletService.getWalletInfo();
    }
    /**
     * 激活钱包（开通钱包，设置支付密码）
     */
    @ApiOperationLog(description = "激活钱包")
    @SaCheckLogin
    @PostMapping("/activate")
    public Response activateWallet(@Valid @RequestBody WalletActivateDTO dto) {
        return walletService.activateWallet(dto);
    }
    
    /**
     * 获取钱包交易记录
     */
    @ApiOperationLog(description = "获取钱包交易记录")
    @SaCheckLogin
    @GetMapping("/transactions")
    public Response getTransactions(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return walletService.getTransactions(pageNo, pageSize);
    }
}

