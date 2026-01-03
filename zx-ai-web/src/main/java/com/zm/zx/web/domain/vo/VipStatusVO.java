package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VIP状态 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VipStatusVO {
    
    /**
     * 是否为VIP
     */
    private Boolean isVip;
    
    /**
     * VIP过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime vipExpireTime;
    
    /**
     * 剩余天数
     */
    private Integer remainingDays;
    
    /**
     * 状态文本
     */
    private String statusText;
}

