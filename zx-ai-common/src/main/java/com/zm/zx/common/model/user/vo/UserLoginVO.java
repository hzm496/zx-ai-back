package com.zm.zx.common.model.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String gender;
    private Integer isVip;
    private LocalDateTime vipExpireTime;
    private String role;
    private String token;
}
