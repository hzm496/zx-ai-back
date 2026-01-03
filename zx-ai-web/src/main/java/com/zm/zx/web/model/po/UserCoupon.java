package com.zm.zx.web.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户优惠券关联表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user_coupon")
public class UserCoupon {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long couponId;
    private Integer status;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private LocalDateTime expireTime;
    private Long orderId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

