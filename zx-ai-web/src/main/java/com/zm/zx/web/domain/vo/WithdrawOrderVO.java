package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现订单VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawOrderVO {
    
    /**
     * 提现订单ID
     */
    private Long id;

    /**
     * 提现流水号
     */
    private String withdrawNo;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 实际到账金额
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
     * 状态名称
     */
    private String statusName;

    /**
     * 提现方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer accountType;

    /**
     * 提现方式名称
     */
    private String accountTypeName;

    /**
     * 提现账户信息（脱敏显示）
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

