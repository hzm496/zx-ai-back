package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.web.model.dto.CourseOrderQueryDTO;
import com.zm.zx.web.service.CourseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员课程订单控制器
 */
@SaCheckRole("admin")
@Slf4j
@RestController
@RequestMapping("/admin/course/order")
@RequiredArgsConstructor
public class AdminCourseOrderController {

    private final CourseOrderService courseOrderService;

    /**
     * 分页查询课程订单列表
     */
    @ApiOperationLog(description = "分页查询课程订单列表")
    @PostMapping("/list")
    public PageResponse getCourseOrderList(@RequestBody CourseOrderQueryDTO queryDTO) {
        log.info("分页查询课程订单列表，参数：{}", queryDTO);
        return courseOrderService.getCourseOrderListForAdmin(queryDTO);
    }
}

