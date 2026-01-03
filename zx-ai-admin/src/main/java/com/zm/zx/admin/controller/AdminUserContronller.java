package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.admin.model.dto.FindUserListDTO;
import com.zm.zx.admin.service.AdminUserService;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.model.user.dto.UserUpdatePasswordDTO;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.UserUpdateInfoDTO;
import com.zm.zx.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@SaCheckRole("admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/user")
public class AdminUserContronller {
    private final UserService userService;
    private final AdminUserService adminUserService;

    /**
     * 更新用户信息
     */
//    @ApiOperationLog(description = "更新用户信息")
//    @PutMapping("/updateUserInfo")
//    public Response updateUserInfo(@RequestBody UserUpdateInfoDTO userUpdateInfoDTO) {
//        return userService.updateUserInfo(userUpdateInfoDTO);
//    }


    /**
     * 获取用户信息(web模块已经提供)
     */
    @ApiOperationLog(description = "获取用户信息")
    @GetMapping("/getUserInfo")
    public Response getUserInfo() {
        return userService.getUserInfo();
    }

    /**
     * 根据id查询用户信息(web模块已经提供)
     */
    @ApiOperationLog(description = "根据id查询用户信息")
    @GetMapping("/getUserById/{id}")
    public Response getUserById(@PathVariable("id") Long id) {
        return userService.getUserInfoById(id);
    }

    /**
     * 退出(web模块已经提供)
     */
    @ApiOperationLog(description = "退出")
    @GetMapping("/logout")
    public Response logout() {
        return userService.logout();
    }

    @ApiOperationLog(description = "修改密码")
    @PostMapping("/updatePassword")
    public Response updatePassword(@Validated @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        return userService.updatePassword(userUpdatePasswordDTO);
    }

    @PostMapping("/finduserList")
    public PageResponse list(@RequestBody FindUserListDTO findUserListDTO) {
        return adminUserService.findUserList(findUserListDTO);
    }

    @DeleteMapping("/deleteUser/{id}")
    public Response deleteUser(@PathVariable("id") Long id) {
        return adminUserService.deleteUser(id);
    }

//    /**
//     * 管理员编辑用户信息
//     */
    @ApiOperationLog(description = "管理员编辑用户信息")
    @PutMapping("/updateUserInfoByAdmin/{userId}")
    public Response updateUserInfoByAdmin(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateInfoDTO userUpdateInfoDTO) {
        return adminUserService.updateUserInfoByAdmin(userId, userUpdateInfoDTO);
    }

}
