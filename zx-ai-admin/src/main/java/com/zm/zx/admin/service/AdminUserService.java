package com.zm.zx.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zm.zx.admin.model.dto.FindUserListDTO;
import com.zm.zx.admin.model.po.AdminUser;
import com.zm.zx.common.model.user.dto.UserLoginDTO;
import com.zm.zx.common.model.user.dto.UserUpdatePasswordDTO;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RegisterDTO;
import com.zm.zx.web.domain.dto.UserUpdateInfoDTO;
import com.zm.zx.web.domain.po.User;

/**
* @author 24690
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-10-12 19:59:42
*/
public interface AdminUserService extends IService<AdminUser> {


    PageResponse findUserList( FindUserListDTO findUserListDTO);

    //删除用户
    Response deleteUser(Long id);

//    //管理员编辑用户信息
    Response updateUserInfoByAdmin(Long userId, UserUpdateInfoDTO userUpdateInfoDTO);


}
