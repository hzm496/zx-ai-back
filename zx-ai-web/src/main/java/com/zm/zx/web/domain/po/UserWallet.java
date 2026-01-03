package com.zm.zx.web.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包表
 * @TableName user_wallet
 */
@TableName(value = "user_wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWallet {
    /**
     * 钱包ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 支付密码（加密存储）
     */
    private String paymentPassword;

    /**
     * 绑定的支付宝账号
     */
    private String alipayAccount;

    /**
     * 支付宝账户姓名
     */
    private String alipayName;

    /**
     * 是否已开通：0-未开通，1-已开通
     */
    private Integer isActivated;

    /**
     * 状态：0-冻结，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

