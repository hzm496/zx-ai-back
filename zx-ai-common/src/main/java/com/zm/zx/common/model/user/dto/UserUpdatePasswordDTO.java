package com.zm.zx.common.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;

}
