package com.zm.zx.web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程订单查询 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseOrderQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 课程标题（模糊查询）
     */
    private String courseTitle;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;

    /**
     * 支付方式：1-余额，2-支付宝
     */
    private Integer payType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}

