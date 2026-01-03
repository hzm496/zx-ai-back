package com.zm.zx.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置更新 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemConfigUpdateDTO {
    
    @NotBlank(message = "配置键不能为空")
    private String configKey;
    
    @NotBlank(message = "配置值不能为空")
    private String configValue;
}

