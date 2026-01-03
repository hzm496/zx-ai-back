package com.zm.zx.common.model.customer.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 客服消息表 PO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("customer_service_message")
public class CustomerServiceMessage {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 发送者类型：1-用户，2-客服
     */
    private Integer senderType;
    
    /**
     * 发送者姓名
     */
    private String senderName;
    
    /**
     * 发送者头像
     */
    private String senderAvatar;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}

