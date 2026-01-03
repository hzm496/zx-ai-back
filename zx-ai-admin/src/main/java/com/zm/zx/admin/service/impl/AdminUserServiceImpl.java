package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.admin.mapper.AdminUserMapper;
import com.zm.zx.admin.model.dto.FindUserListDTO;
import com.zm.zx.admin.model.po.AdminUser;
import com.zm.zx.admin.model.vo.FindUserListVO;
import com.zm.zx.admin.service.AdminUserService;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.UserUpdateInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author 24690
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-10-12 19:59:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Override
    public PageResponse findUserList(FindUserListDTO findUserListDTO) {
        Integer pageSize = findUserListDTO.getPageSize();
        Integer pageNum = findUserListDTO.getPageNum();
        String username = findUserListDTO.getUsername();
        Integer gender = findUserListDTO.getGender();
        Integer isVip = findUserListDTO.getIsVip();
        Integer status = findUserListDTO.getStatus();
        Page<AdminUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AdminUser> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(username), AdminUser::getUsername, username);
        lqw.eq(Objects.nonNull(gender), AdminUser::getGender, gender);
        lqw.eq(Objects.nonNull(isVip), AdminUser::getIsVip, isVip);
        lqw.eq(Objects.nonNull(status), AdminUser::getStatus, status);
        Page<AdminUser> adminUserPage = this.page(page, lqw);
        List<AdminUser> records = page.getRecords();
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }
        List<FindUserListVO> findUserListVOS = records.stream().map(adminUser -> {
            FindUserListVO findUserListVO = BeanUtil.copyProperties(adminUser, FindUserListVO.class);
            return findUserListVO;
        }).collect(Collectors.toList());
        return PageResponse.success(findUserListVOS, pageNum, adminUserPage.getTotal(), pageSize);
    }

    @Override
    public Response deleteUser(Long id) {
        boolean remove = this.removeById(id);
        if (!remove) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }
        return Response.success();
    }

    @Override
    public Response updateUserInfoByAdmin(Long userId, UserUpdateInfoDTO userUpdateInfoDTO) {
        // 检查用户是否存在
        AdminUser user = this.getById(userId);
        if (user == null) {
            throw new BizException(ResponseEnum.USER_NOT_EXIST);
        }

        // 更新用户信息
        AdminUser updateUser = new AdminUser();
        updateUser.setId(userId);

        // 只更新非空字段
        if (StrUtil.isNotBlank(userUpdateInfoDTO.getNickname())) {
            updateUser.setNickname(userUpdateInfoDTO.getNickname());
        }
        if (StrUtil.isNotBlank(userUpdateInfoDTO.getAvatar())) {
            updateUser.setAvatar(userUpdateInfoDTO.getAvatar());
        }
        if (StrUtil.isNotBlank(userUpdateInfoDTO.getPhone())) {
            updateUser.setPhone(userUpdateInfoDTO.getPhone());
        }
        if (StrUtil.isNotBlank(userUpdateInfoDTO.getEmail())) {
            updateUser.setEmail(userUpdateInfoDTO.getEmail());
        }
        if (userUpdateInfoDTO.getGender() != null) {
            updateUser.setGender(userUpdateInfoDTO.getGender());
        }

        boolean success = this.updateById(updateUser);
        if (!success) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }

        return Response.success();
    }

}




