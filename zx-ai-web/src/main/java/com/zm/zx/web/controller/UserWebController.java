package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.model.user.dto.UserUpdatePasswordDTO;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RegisterDTO;
import com.zm.zx.common.model.user.dto.UserLoginDTO;
import com.zm.zx.web.domain.dto.UserUpdateInfoDTO;
import com.zm.zx.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/web/user")
public class UserWebController {

    private final UserService userService;

    /**
     * 登录
     */
    @ApiOperationLog(description = "登录")
    @PostMapping("/login")
    public Response login(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }

    @ApiOperationLog(description = "注册")
    @PostMapping("/register")
    public Response register(@Validated @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @ApiOperationLog(description = "登出")
    @GetMapping("/logout")
    public Response logout() {
        userService.logout();
        return Response.success();
    }

    @ApiOperationLog(description = "获取用户信息")
    @GetMapping("/getUserInfo")
    public Response getUserInfo() {
        return userService.getUserInfo();
    }

    @ApiOperationLog(description = "根据id获取用户信息")
    @GetMapping("/getUserInfoById/{id}")
    public Response getUserInfoById(@PathVariable("id") Long id) {
        return userService.getUserInfoById(id);
    }

    /**
     * 修改个人信息
     */
    @ApiOperationLog(description = "修改个人信息")
    @PostMapping("/updateUserInfo")
    public Response updateUserInfo(@Validated @RequestBody UserUpdateInfoDTO userUpdateInfoDTO) {
        return userService.updateUserInfo(userUpdateInfoDTO);
    }

    /**
     * 修改密码
     */
    @ApiOperationLog(description = "修改密码")
    @PostMapping("/updatePassword")
    public Response updatePassword(@Validated @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        return userService.updatePassword(userUpdatePasswordDTO);
    }
}
