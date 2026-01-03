package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.MembershipAddDTO;
import com.zm.zx.admin.model.dto.MembershipUpdateDTO;
import com.zm.zx.admin.service.MembershipService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 会员配置管理 Controller（后台）
 */
@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/membership")
public class MembershipController {
    
    private final MembershipService membershipService;
    
    /**
     * 获取会员配置列表
     */
    @ApiOperationLog(description = "获取会员配置列表")
    @GetMapping("/list")
    public Response list() {
        return membershipService.getMembershipList();
    }
    
    /**
     * 添加会员配置
     */
    @ApiOperationLog(description = "添加会员配置")
    @PostMapping("/add")
    public Response add(@Validated @RequestBody MembershipAddDTO membershipAddDTO) {
        return membershipService.addMembership(membershipAddDTO);
    }
    
    /**
     * 更新会员配置
     */
    @ApiOperationLog(description = "更新会员配置")
    @PutMapping("/update")
    public Response update(@Validated @RequestBody MembershipUpdateDTO membershipUpdateDTO) {
        return membershipService.updateMembership(membershipUpdateDTO);
    }
    
    /**
     * 删除会员配置
     */
    @ApiOperationLog(description = "删除会员配置")
    @DeleteMapping("/delete/{id}")
    public Response delete(@PathVariable Long id) {
        return membershipService.deleteMembership(id);
    }
    
    /**
     * 根据ID获取会员配置
     */
    @ApiOperationLog(description = "根据ID获取会员配置")
    @GetMapping("/getById/{id}")
    public Response getById(@PathVariable Long id) {
        return membershipService.getMembershipById(id);
    }
}

