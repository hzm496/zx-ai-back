package com.zm.zx.web.service;

import com.zm.zx.common.model.user.dto.UserUpdatePasswordDTO;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RegisterDTO;
import com.zm.zx.common.model.user.dto.UserLoginDTO;
import com.zm.zx.web.domain.dto.UserUpdateInfoDTO;
import com.zm.zx.web.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 24690
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-10-12 19:59:42
*/
public interface UserService extends IService<User> {

    /*登录*/
    Response login(UserLoginDTO userLoginDTO);
    /*注册*/
    Response register(RegisterDTO registerDTO);
    /*退出*/
    Response logout();
    /*获取用户个人信息*/
    Response getUserInfo();
    /*获取用户信息ById*/
    Response getUserInfoById(Long id);
    /*更新用户信息*/
    Response updateUserInfo(UserUpdateInfoDTO userUpdateInfoDTO);
    /*更新密码*/
    Response updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO);
}
