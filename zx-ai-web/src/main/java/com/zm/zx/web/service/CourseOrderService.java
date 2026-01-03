package com.zm.zx.web.service;

import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.model.dto.CourseOrderQueryDTO;
import com.zm.zx.web.model.dto.CreateCourseOrderDTO;

/**
 * 课程订单服务接口
 */
public interface CourseOrderService {

    /**
     * 创建课程订单并支付
     */
    Response createAndPayOrder(CreateCourseOrderDTO dto);

    /**
     * 支付宝回调处理
     */
    Response alipayCallback(String orderNo, String alipayTradeNo);

    /**
     * 分页获取用户的课程订单列表
     */
    PageResponse getUserCourseOrdersPage(Integer pageNo, Integer pageSize);

    /**
     * 管理员分页查询课程订单列表
     */
    PageResponse getCourseOrderListForAdmin(CourseOrderQueryDTO queryDTO);

    /**
     * 取消订单
     */
    Response cancelOrder(String orderNo);

    /**
     * 继续支付未支付的订单（获取订单信息）
     */
    Response continuePayOrder(String orderNo);

    /**
     * 继续支付 - 余额支付
     */
    Response continueBalancePay(String orderNo, String payPassword);
}
