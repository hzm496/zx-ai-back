package com.zm.zx.web.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.constant.DefaultRole;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.model.user.dto.UserUpdatePasswordDTO;
import com.zm.zx.common.model.user.vo.UserInfoVO;
import com.zm.zx.common.utils.LoginUserContextHolder;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.RegisterDTO;
import com.zm.zx.common.model.user.dto.UserLoginDTO;
import com.zm.zx.web.domain.dto.UserUpdateInfoDTO;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.common.model.user.vo.UserLoginVO;
import com.zm.zx.web.enums.RoleEnum;
import com.zm.zx.web.enums.UserStatusEnum;
import com.zm.zx.web.mapper.UserMapper;
import com.zm.zx.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author 24690
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-10-12 19:59:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordEncoder encoder;

    @Override
    public Response login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        log.info("用户尝试登录: {}", username);

        // 1. 查询用户
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, username);
        User user = this.getOne(lqw);

        // 2. 用户不存在，统一提示"账号或密码错误"，避免暴露用户是否存在
        if (Objects.isNull(user)) {
            log.warn("登录失败，用户不存在: {}", username);
            throw new BizException(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 3. 密码校验（先校验密码，统一错误提示）
        if (!encoder.matches(password, user.getPassword())) {
            log.warn("登录失败，密码错误: {}", username);
            throw new BizException(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 4. 用户状态校验
        if (!Objects.equals(user.getStatus(), UserStatusEnum.NORMAL.getStatus())) {
            log.warn("登录失败，账号已冻结: {}", username);
            throw new BizException(ResponseEnum.USER_FROZEN);
        }

        // 5. 验证角色是否有效
        String role = userLoginDTO.getRole();
        String userRole = user.getRole();
        String admin = RoleEnum.ADMIN.getRole();
        if (Objects.equals(role, admin) && !Objects.equals(userRole, admin)) {
            log.warn("登录失败，用户无权限: {}", username);
            throw new BizException(ResponseEnum.NO_AUTH);
        }

        // 6. 执行登录
        StpUtil.login(user.getId());
        
        // 7. 将用户角色存储到 Redis（使用 Sa-Token 提供的方法）
        // key: satoken:login:role:{userId}
        SaManager.getSaTokenDao().set("satoken:login:role:" + user.getId(), user.getRole(), StpUtil.getTokenTimeout());
        log.info("用户角色已存储到Redis: userId={}, role={}", user.getId(), user.getRole());
        
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();
        // 返回带 Bearer 前缀的完整 token，与配置的 token-prefix 保持一致
        String token = "Bearer " + tokenValue;
        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
        // 8. 构建返回对象
        userLoginVO.setToken(token);

        log.info("登录成功: {}, 角色: {}", username, userRole);
        return Response.success(userLoginVO);
    }

    @Override
    public Response register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();

        log.info("用户尝试注册: {}", username);

        // 1. 校验用户名格式（可选，根据业务需求）
        if (username.length() < 3 || username.length() > 20) {
            throw new BizException("用户名长度必须在3-20个字符之间");
        }

        // 2. 校验密码强度
        if (password.length() < 6) {
            throw new BizException("密码长度不能少于6位");
        }

        // 3. 检查用户名是否已存在
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, username);
        User existUser = this.getOne(lqw);
        if (Objects.nonNull(existUser)) {
            log.warn("注册失败，用户名已存在: {}", username);
            throw new BizException(ResponseEnum.USER_EXIST);
        }

        // 4. 加密密码
        String encodedPassword = encoder.encode(password);

        // 5. 创建新用户
        User registerUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .nickname(DefaultRole.COMMON_USER_NAME)
                .role(DefaultRole.COMMON_USER_ROLE)
                .status(UserStatusEnum.NORMAL.getStatus())
                .build();

        // 6. 保存用户
        boolean saved = save(registerUser);
        if (!saved) {
            log.error("注册失败，保存用户失败: {}", username);
            throw new BizException("注册失败，请稍后重试");
        }

        log.info("注册成功: {}", username);
        return Response.success("注册成功");
    }

    @Override
    public Response logout() {
        Long userId = LoginUserContextHolder.getUserId();
        
        // 清除 Redis 中的角色信息
        String roleKey = "satoken:login:role:" + userId;
        SaManager.getSaTokenDao().delete(roleKey);
        log.info("用户 {} 的角色信息已从Redis清除", userId);
        
        // 执行登出
        StpUtil.logout(userId);
        return Response.success();
    }

    @Override
    public Response getUserInfo() {
        Long userId = LoginUserContextHolder.getUserId();
        User user = getById(userId);
        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        return Response.success(userInfoVO);
    }

    @Override
    public Response getUserInfoById(Long id) {
        User user = getById(id);
        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        return Response.success(userInfoVO);
    }

    @Override
    public Response updateUserInfo(UserUpdateInfoDTO userUpdateInfoDTO) {
        Long userId = LoginUserContextHolder.getUserId();
        User user = BeanUtil.copyProperties(userUpdateInfoDTO, User.class);
        user.setId(userId);
        boolean updated = updateById(user);
        if (!updated) {
            throw new BizException(ResponseEnum.USER_UPDATE_FAIL);
        }
        return Response.success();
    }

    @Override
    public Response updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO) {
        Long userId = LoginUserContextHolder.getUserId();
        String newPassword = userUpdatePasswordDTO.getNewPassword();
        String oldPassword = userUpdatePasswordDTO.getOldPassword();
        User user = getById(userId);
        String password = user.getPassword();
        if (!encoder.matches(oldPassword, password)) {
            /*旧密码错误*/
            throw new BizException(ResponseEnum.OLD_PASSWORD_ERROR);
        }
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        boolean updated = updateById(user);
        if (!updated) {
            throw new BizException(ResponseEnum.PASSWORD_UPDATE_FAIL);
        }
        return Response.success();
    }
}




