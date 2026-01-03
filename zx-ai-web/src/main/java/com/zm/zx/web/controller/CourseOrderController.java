package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.model.dto.CreateCourseOrderDTO;
import com.zm.zx.web.service.CourseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 课程订单 Controller
 */
@Slf4j
@RestController
@RequestMapping("/web/course/order")
@RequiredArgsConstructor
public class CourseOrderController {

    private final CourseOrderService courseOrderService;

    /**
     * 创建课程订单并支付
     */
    @ApiOperationLog(description = "创建课程订单")
    @PostMapping("/create")
    public Response createOrder(@Validated @RequestBody CreateCourseOrderDTO dto) {
        return courseOrderService.createAndPayOrder(dto);
    }

    /**
     * 支付宝支付回调
     */
    @ApiOperationLog(description = "支付宝回调")
    @PostMapping("/alipay/callback")
    public Response alipayCallback(@RequestParam("orderNo") String orderNo, @RequestParam("alipayTradeNo") String alipayTradeNo) {
        return courseOrderService.alipayCallback(orderNo, alipayTradeNo);
    }

    /**
     * 分页查询用户的课程订单列表
     */
    @ApiOperationLog(description = "分页查询课程订单列表")
    @GetMapping("/list")
    public PageResponse getUserCourseOrdersPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return courseOrderService.getUserCourseOrdersPage(pageNo, pageSize);
    }

    /**
     * 取消订单
     */
    @ApiOperationLog(description = "取消课程订单")
    @PostMapping("/cancel")
    public Response cancelOrder(@RequestParam("orderNo") String orderNo) {
        return courseOrderService.cancelOrder(orderNo);
    }

    /**
     * 继续支付未支付的订单（获取订单信息）
     */
    @ApiOperationLog(description = "继续支付课程订单")
    @GetMapping("/continue-pay")
    public Response continuePayOrder(@RequestParam("orderNo") String orderNo) {
        return courseOrderService.continuePayOrder(orderNo);
    }

    /**
     * 继续支付 - 余额支付
     */
    @ApiOperationLog(description = "继续支付-余额支付")
    @PostMapping("/continue-pay/balance")
    public Response continueBalancePay(@RequestParam("orderNo") String orderNo,
                                       @RequestParam("payPassword") String payPassword) {
        return courseOrderService.continueBalancePay(orderNo, payPassword);
    }
}

