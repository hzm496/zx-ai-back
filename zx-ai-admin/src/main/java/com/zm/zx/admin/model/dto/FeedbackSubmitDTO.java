package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户提交反馈 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackSubmitDTO {
    
    /**
     * 用户名（未登录用户需填写）
     */
    private String username;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    private String content;
}

