package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包信息 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletVO {
    
    /**
     * 钱包ID
     */
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
     * 是否已开通：0-未开通，1-已开通
     */
    private Integer isActivated;
    
    /**
     * 状态：0-冻结，1-正常
     */
    private Integer status;
    
    /**
     * 是否设置了支付密码
     */
    private Boolean hasPaymentPassword;
    
    /**
     * 绑定的支付宝账号
     */
    private String alipayAccount;
    
    /**
     * 支付宝账户姓名
     */
    private String alipayName;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

