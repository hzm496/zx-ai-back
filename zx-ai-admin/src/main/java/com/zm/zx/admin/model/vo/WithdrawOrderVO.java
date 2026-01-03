package com.zm.zx.admin.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现订单VO
 */
@Data
public class WithdrawOrderVO {
    /**
     * 提现订单ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 提现流水号
     */
    private String withdrawNo;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 实际到账金额（扣除手续费）
     */
    private BigDecimal actualAmount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 状态：0-待处理，1-已完成，2-已拒绝
     */
    private Integer status;

    /**
     * 提现方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer accountType;

    /**
     * 提现账户信息
     */
    private String accountInfo;

    /**
     * 提现账户姓名
     */
    private String accountName;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 处理管理员ID
     */
    private Long processAdminId;
    
    /**
     * 支付宝转账单号
     */
    private String alipayOrderId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

