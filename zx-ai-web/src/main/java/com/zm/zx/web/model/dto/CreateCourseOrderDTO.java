package com.zm.zx.web.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 创建课程订单 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCourseOrderDTO {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 支付方式：1-余额，2-支付宝
     */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    /**
     * 支付密码（余额支付时需要）
     */
    private String payPassword;
    
    /**
     * 优惠券ID（可选）
     */
    private Long couponId;
}

