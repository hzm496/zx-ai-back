package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包交易记录 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransactionVO {
    
    /**
     * 交易ID
     */
    private Long id;
    
    /**
     * 交易流水号
     */
    private String transactionNo;
    
    /**
     * 交易类型：1-充值，2-消费，3-退款
     */
    private Integer type;
    
    /**
     * 交易类型名称
     */
    private String typeName;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
    /**
     * 交易前余额
     */
    private BigDecimal balanceBefore;
    
    /**
     * 交易后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 业务类型
     */
    private String businessType;
    
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

