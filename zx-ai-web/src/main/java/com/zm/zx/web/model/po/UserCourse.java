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
 * 用户课程关联实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user_course")
public class UserCourse {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 获取方式：1-购买，2-VIP免费，3-赠送，4-兑换
     */
    private Integer sourceType;

    /**
     * 关联的订单ID（如果是购买）
     */
    private Long orderId;

    /**
     * 过期时间（NULL表示永久有效）
     */
    private LocalDateTime expireTime;

    /**
     * 是否有效：0-已过期/失效，1-有效
     */
    private Integer isValid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

