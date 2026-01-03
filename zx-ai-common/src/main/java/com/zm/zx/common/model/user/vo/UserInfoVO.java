package com.zm.zx.common.model.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zm.zx.common.annotation.ApiOperationLog;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 角色
     */
    private String role;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    private Integer status;

    /**
     * 是否会员：0-否，1-是
     */
    private Integer isVip;

    /**
     * 会员过期时间
     */
    private LocalDate vipExpireTime;





    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
