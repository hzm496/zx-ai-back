package com.zm.zx.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客服消息 VO（通用）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerServiceMessageVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private Integer senderType;
    private String senderName;
    private String senderAvatar;
    private String content;
    private Integer isRead;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}


