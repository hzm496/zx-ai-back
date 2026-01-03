package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.constant.MQConstants;
import com.zm.zx.common.mapper.CourseMapper;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.web.domain.po.UserWallet;
import com.zm.zx.web.domain.po.WalletTransaction;
import com.zm.zx.web.mapper.*;
import com.zm.zx.web.mapper.UserCouponMapper;
import com.zm.zx.web.mapper.WebCouponMapper;
import com.zm.zx.web.model.dto.CourseOrderQueryDTO;
import com.zm.zx.web.model.dto.CreateCourseOrderDTO;
import com.zm.zx.web.model.po.Coupon;
import com.zm.zx.web.model.po.CourseOrder;
import com.zm.zx.web.model.po.UserCoupon;
import com.zm.zx.web.model.po.UserCourse;
import com.zm.zx.web.model.vo.CourseOrderVO;
import com.zm.zx.web.service.CourseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseOrderServiceImpl implements CourseOrderService {

    private final CourseOrderMapper courseOrderMapper;
    private final UserCourseMapper userCourseMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final UserWalletMapper walletMapper;
    private final WalletTransactionMapper transactionMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserCouponMapper userCouponMapper;
    private final WebCouponMapper couponMapper;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createAndPayOrder(CreateCourseOrderDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 创建课程订单，课程ID：{}，支付方式：{}", userId, dto.getCourseId(), dto.getPayType());

        // 1. 检查课程是否存在
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            return Response.fail("课程不存在" );
        }

        // 2. 检查课程是否已购买
        boolean hasCourse = userCourseMapper.checkUserHasCourse(userId, dto.getCourseId());
        if (hasCourse) {
            return Response.fail("您已拥有该课程");
        }

        // 3. 检查课程是否免费
        if (course.getIsFree() == 1) {
            return Response.fail("该课程为免费课程，无需购买" );
        }

        // 4. 计算优惠后的价格
        BigDecimal finalPrice = course.getPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;
        UserCoupon userCoupon = null;
        Coupon coupon = null;
        
        if (dto.getCouponId() != null) {
            // 验证并计算优惠
            Map<String, Object> couponResult = validateAndCalculateCoupon(userId, dto.getCouponId(), course.getPrice());
            if (couponResult.get("error") != null) {
                return Response.fail((String) couponResult.get("error"));
            }
            finalPrice = (BigDecimal) couponResult.get("finalPrice");
            discountAmount = (BigDecimal) couponResult.get("discountAmount");
            userCoupon = (UserCoupon) couponResult.get("userCoupon");
            coupon = (Coupon) couponResult.get("coupon");
        }
        
        // 5. 根据支付方式处理支付
        if (dto.getPayType() == 1) {
            // 余额支付：先验证，再创建订单
            return balancePayWithValidation(userId, course, dto.getPayPassword(), 
                finalPrice, discountAmount, userCoupon, coupon);
        } else if (dto.getPayType() == 2) {
            // 支付宝支付：先创建订单，等待回调
            return alipayPayWithOrder(userId, course, finalPrice, discountAmount, userCoupon, coupon);
        } else {
            return Response.fail("不支持的支付方式" );
        }
    }

    /**
     * 验证并计算优惠券优惠
     */
    private Map<String, Object> validateAndCalculateCoupon(Long userId, Long userCouponId, BigDecimal coursePrice) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            result.put("error", "优惠券不存在");
            return result;
        }
        
        // 2. 检查优惠券状态
        if (userCoupon.getStatus() != 0) {
            result.put("error", "优惠券已使用");
            return result;
        }
        
        if (userCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
            result.put("error", "优惠券已过期");
            return result;
        }
        
        // 3. 查询优惠券详情
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null || coupon.getStatus() != 1) {
            result.put("error", "优惠券已下架");
            return result;
        }
        
        // 4. 检查是否满足使用条件
        if (coursePrice.compareTo(coupon.getMinAmount()) < 0) {
            result.put("error", "订单金额不足¥" + coupon.getMinAmount());
            return result;
        }
        
        // 5. 计算优惠金额
        BigDecimal discountAmount;
        if (coupon.getType() == 1) {
            // 满减券：直接减免
            discountAmount = coupon.getDiscountAmount();
        } else {
            // 折扣券：原价 * (1 - 折扣率)，不超过最大优惠金额
            discountAmount = coursePrice.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()))
                .setScale(2, java.math.RoundingMode.HALF_UP);
            
            if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                discountAmount = coupon.getMaxDiscount();
            }
        }
        
        // 6. 计算最终价格
        BigDecimal finalPrice = coursePrice.subtract(discountAmount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }
        
        result.put("finalPrice", finalPrice);
        result.put("discountAmount", discountAmount);
        result.put("userCoupon", userCoupon);
        result.put("coupon", coupon);
        
        return result;
    }
    
    /**
     * 余额支付（先验证再创建订单）
     */
    private Response balancePayWithValidation(Long userId, Course course, String payPassword,
                                               BigDecimal finalPrice, BigDecimal discountAmount,
                                               UserCoupon userCoupon, Coupon coupon) {
        // 1. 检查钱包是否开通
        UserWallet wallet = walletMapper.selectOne(
                new LambdaQueryWrapper<UserWallet>().eq(UserWallet::getUserId, userId)
        );

        if (wallet == null || wallet.getIsActivated() == 0) {
            return Response.fail("请先开通钱包" );
        }

        // 2. 验证支付密码
        if (payPassword == null || payPassword.isEmpty()) {
            return Response.fail("请输入支付密码" );
        }

        if (!passwordEncoder.matches(payPassword, wallet.getPaymentPassword())) {
            return Response.fail("支付密码错误" );
        }

        // 3. 检查余额是否足够（使用优惠后的价格）
        if (wallet.getBalance().compareTo(finalPrice) < 0) {
            return Response.fail("余额不足");
        }

        // === 验证通过，开始创建订单并扣款 ===
        
        // 4. 创建订单
        String orderNo = "COURSE" + System.currentTimeMillis() + userId;
        CourseOrder order = CourseOrder.builder()
                .orderNo(orderNo)
                .userId(userId)
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseCover(course.getCover())
                .originalPrice(course.getOriginalPrice())
                .payAmount(finalPrice) // 使用优惠后的价格
                .couponId(userCoupon != null ? userCoupon.getId() : null) // 记录优惠券ID
                .couponAmount(discountAmount) // 记录优惠金额
                .payType(1) // 余额支付
                .status(1) // 已支付（余额支付验证通过后直接标记为已支付）
                .payTime(LocalDateTime.now())
                .build();
        courseOrderMapper.insert(order);

        // 5. 扣除余额
        BigDecimal newBalance = wallet.getBalance().subtract(order.getPayAmount());
        wallet.setBalance(newBalance);
        walletMapper.updateById(wallet);

        // 6. 记录交易流水
        String transactionNo = "TXN" + System.currentTimeMillis() + userId;
        String remark = "购买课程：" + order.getCourseTitle();
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            remark += "（使用优惠券：" + coupon.getName() + "，优惠¥" + discountAmount + "）";
        }
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(userId)
                .transactionNo(transactionNo)
                .type(2) // 消费
                .amount(order.getPayAmount())
                .balanceBefore(wallet.getBalance().add(order.getPayAmount()))
                .balanceAfter(newBalance)
                .businessType("COURSE_PURCHASE")
                .businessId(order.getId())
                .remark(remark)
                .build();
        transactionMapper.insert(transaction);
        
        // 7. 标记优惠券为已使用
        if (userCoupon != null) {
            userCoupon.setStatus(1); // 已使用
            userCoupon.setUseTime(LocalDateTime.now());
            userCoupon.setOrderId(order.getId());
            userCoupon.setUpdateTime(LocalDateTime.now());
            userCouponMapper.updateById(userCoupon);
            log.info("优惠券 {} 已使用", userCoupon.getId());
        }

        // 7. 添加用户课程关联
        UserCourse userCourse = UserCourse.builder()
                .userId(userId)
                .courseId(order.getCourseId())
                .sourceType(1) // 购买
                .orderId(order.getId())
                .isValid(1)
                .build();
        userCourseMapper.insert(userCourse);

        log.info("用户 {} 余额支付成功，订单号：{}", userId, order.getOrderNo());

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payType", 1);
        result.put("status", "success");

        return Response.success(result);
    }

    /**
     * 支付宝支付（创建订单等待支付）
     */
    private Response alipayPayWithOrder(Long userId, Course course, 
                                         BigDecimal finalPrice, BigDecimal discountAmount,
                                         UserCoupon userCoupon, Coupon coupon) {
        // 创建订单（待支付状态）
        String orderNo = "COURSE" + System.currentTimeMillis() + userId;
        CourseOrder order = CourseOrder.builder()
                .orderNo(orderNo)
                .userId(userId)
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseCover(course.getCover())
                .originalPrice(course.getOriginalPrice())
                .payAmount(finalPrice) // 使用优惠后的价格
                .couponId(userCoupon != null ? userCoupon.getId() : null)
                .couponAmount(discountAmount)
                .payType(2) // 支付宝支付
                .status(0) // 待支付（等待支付宝回调）
                .build();
        courseOrderMapper.insert(order);
        
        // 标记优惠券为已使用（支付宝创建订单时即使用）
        if (userCoupon != null) {
            userCoupon.setStatus(1); // 已使用
            userCoupon.setUseTime(LocalDateTime.now());
            userCoupon.setOrderId(order.getId());
            userCoupon.setUpdateTime(LocalDateTime.now());
            userCouponMapper.updateById(userCoupon);
            log.info("优惠券 {} 已使用（支付宝订单）", userCoupon.getId());
        }

        log.info("创建支付宝订单，订单号：{}，原价：{}，优惠：{}，实付：{}", 
            order.getOrderNo(), course.getPrice(), discountAmount, finalPrice);

        // 发送延迟消息，2分钟后检查订单状态，如果未支付则自动取消
        sendOrderTimeoutCancelMessage(order.getOrderNo(), 2);

        // 返回订单号，前端调用支付宝支付接口
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payType", 2);
        result.put("courseTitle", order.getCourseTitle());
        result.put("payAmount", order.getPayAmount());
        result.put("originalPrice", course.getPrice());
        result.put("discountAmount", discountAmount);

        return Response.success(result);
    }
    
    /**
     * 发送订单超时取消延迟消息
     * @param orderNo 订单号
     * @param delayMinutes 延迟分钟数
     */
    private void sendOrderTimeoutCancelMessage(String orderNo, int delayMinutes) {
        try {
            // RocketMQ 延迟级别：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
            // delayLevel=6 表示延迟2分钟
            rocketMQTemplate.syncSend(
                MQConstants.TOPIC_ORDER_TIMEOUT_CANCEL,
                MessageBuilder.withPayload(orderNo).build(),
                3000, // 发送超时时间3秒
                6     // 延迟级别6：2分钟
            );
            log.info("发送订单超时取消延迟消息成功，订单号：{}，延迟{}分钟", orderNo, delayMinutes);
        } catch (Exception e) {
            log.error("发送订单超时取消延迟消息失败，订单号：{}", orderNo, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response alipayCallback(String orderNo, String alipayTradeNo) {
        log.info("支付宝回调，订单号：{}，支付宝交易号：{}", orderNo, alipayTradeNo);

        // 1. 查询订单
        CourseOrder order = courseOrderMapper.selectOne(
                new LambdaQueryWrapper<CourseOrder>().eq(CourseOrder::getOrderNo, orderNo)
        );

        if (order == null) {
            return Response.fail("订单不存在");
        }

        if (order.getStatus() == 1) {
            return Response.success("订单已支付");
        }

        // 2. 更新订单状态
        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        order.setAlipayTradeNo(alipayTradeNo);
        courseOrderMapper.updateById(order);

        // 3. 添加用户课程关联
        UserCourse userCourse = UserCourse.builder()
                .userId(order.getUserId())
                .courseId(order.getCourseId())
                .sourceType(1) // 购买
                .orderId(order.getId())
                .isValid(1)
                .build();
        userCourseMapper.insert(userCourse);

        log.info("支付宝支付成功，订单号：{}", orderNo);

        return Response.success("支付成功");
    }

    @Override
    public PageResponse getUserCourseOrdersPage(Integer pageNo, Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("分页查询用户 {} 的课程订单列表，页码：{}，每页数量：{}", userId, pageNo, pageSize);

        Page<CourseOrderVO> page = new Page<>(pageNo, pageSize);
        IPage<CourseOrderVO> resultPage = courseOrderMapper.findUserCourseOrdersPage(page, userId);

        List<CourseOrderVO> records = resultPage.getRecords();
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNo, 0, pageSize);
        }

        // 设置订单操作权限
        records.forEach(order -> {
            // 待支付状态：可以支付、可以取消
            if (order.getStatus() == 0) {
                order.setCanPay(true);
                order.setCanCancel(true);
            } else {
                order.setCanPay(false);
                order.setCanCancel(false);
            }
        });

        return PageResponse.success(records, pageNo, resultPage.getTotal(), pageSize);
    }

    @Override
    public PageResponse getCourseOrderListForAdmin(CourseOrderQueryDTO queryDTO) {
        log.info("管理员分页查询课程订单列表，参数：{}", queryDTO);

        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();

        Page<CourseOrderVO> page = new Page<>(pageNum, pageSize);
        IPage<CourseOrderVO> orderPage = courseOrderMapper.findCourseOrderListForAdmin(page, queryDTO);

        List<CourseOrderVO> records = orderPage.getRecords();

        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }
        records.forEach(order->{
            Long userId = order.getUserId();
            User user = userMapper.selectById(userId);
            order.setUsername(user.getUsername());
        });
        return PageResponse.success(records, pageNum, orderPage.getTotal(), pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response cancelOrder(String orderNo) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 取消订单，订单号：{}", userId, orderNo);

        // 1. 查询订单
        LambdaQueryWrapper<CourseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOrder::getOrderNo, orderNo)
                .eq(CourseOrder::getUserId, userId);
        CourseOrder order = courseOrderMapper.selectOne(queryWrapper);

        if (order == null) {
            return Response.fail("订单不存在");
        }

        // 2. 只有待支付状态的订单才能取消
        if (order.getStatus() != 0) {
            return Response.fail("订单状态不允许取消");
        }

        // 3. 取消订单
        order.setStatus(2); // 2-已取消
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        courseOrderMapper.updateById(order);

        // 4. 如果使用了优惠券，释放优惠券
        if (order.getCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(order.getCouponId());
            if (userCoupon != null && userCoupon.getStatus() == 1) {
                userCoupon.setStatus(0); // 恢复为未使用
                userCoupon.setUseTime(null);
                userCoupon.setOrderId(null);
                userCoupon.setUpdateTime(LocalDateTime.now());
                userCouponMapper.updateById(userCoupon);
                log.info("释放优惠券，优惠券ID：{}", userCoupon.getId());
            }
        }

        log.info("订单取消成功，订单号：{}", orderNo);
        return Response.success("订单已取消");
    }

    @Override
    public Response continuePayOrder(String orderNo) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 继续支付订单，订单号：{}", userId, orderNo);

        // 1. 查询订单
        LambdaQueryWrapper<CourseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOrder::getOrderNo, orderNo)
                .eq(CourseOrder::getUserId, userId);
        CourseOrder order = courseOrderMapper.selectOne(queryWrapper);

        if (order == null) {
            return Response.fail("订单不存在");
        }

        // 2. 只有待支付状态的订单才能继续支付
        if (order.getStatus() != 0) {
            return Response.fail("订单状态不允许支付");
        }

        // 3. 检查课程是否已购买
        boolean hasCourse = userCourseMapper.checkUserHasCourse(userId, order.getCourseId());
        if (hasCourse) {
            return Response.fail("您已拥有该课程");
        }

        // 4. 返回订单信息，前端显示支付对话框
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payType", order.getPayType());
        result.put("courseId", order.getCourseId());
        result.put("courseTitle", order.getCourseTitle());
        result.put("courseCover", order.getCourseCover());
        result.put("originalPrice", order.getOriginalPrice());
        result.put("payAmount", order.getPayAmount());

        return Response.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response continueBalancePay(String orderNo, String payPassword) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 余额支付继续支付订单，订单号：{}", userId, orderNo);

        // 1. 查询订单
        LambdaQueryWrapper<CourseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOrder::getOrderNo, orderNo)
                .eq(CourseOrder::getUserId, userId);
        CourseOrder order = courseOrderMapper.selectOne(queryWrapper);

        if (order == null) {
            return Response.fail("订单不存在");
        }

        // 2. 只有待支付状态的订单才能继续支付
        if (order.getStatus() != 0) {
            return Response.fail("订单状态不允许支付");
        }

        // 3. 检查课程是否已购买
        boolean hasCourse = userCourseMapper.checkUserHasCourse(userId, order.getCourseId());
        if (hasCourse) {
            return Response.fail("您已拥有该课程");
        }

        // 4. 检查钱包是否开通
        UserWallet wallet = walletMapper.selectOne(
                new LambdaQueryWrapper<UserWallet>().eq(UserWallet::getUserId, userId)
        );

        if (wallet == null || wallet.getIsActivated() == 0) {
            return Response.fail("请先开通钱包");
        }

        // 5. 验证支付密码
        if (payPassword == null || payPassword.isEmpty()) {
            return Response.fail("请输入支付密码");
        }

        if (!passwordEncoder.matches(payPassword, wallet.getPaymentPassword())) {
            return Response.fail("支付密码错误");
        }

        // 6. 检查余额是否足够
        if (wallet.getBalance().compareTo(order.getPayAmount()) < 0) {
            return Response.fail("余额不足");
        }

        // === 验证通过，开始支付 ===

        // 7. 更新订单状态为已支付
        order.setStatus(1);
        order.setPayType(1); // 余额支付
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        courseOrderMapper.updateById(order);

        // 8. 扣除余额
        BigDecimal newBalance = wallet.getBalance().subtract(order.getPayAmount());
        wallet.setBalance(newBalance);
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);

        // 9. 记录交易流水
        String transactionNo = "TXN" + System.currentTimeMillis() + userId;
        String remark = "购买课程：" + order.getCourseTitle();
        if (order.getCouponAmount() != null && order.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
            remark += "（已使用优惠券，优惠¥" + order.getCouponAmount() + "）";
        }
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(userId)
                .transactionNo(transactionNo)
                .type(2) // 消费
                .amount(order.getPayAmount())
                .balanceBefore(wallet.getBalance().add(order.getPayAmount()))
                .balanceAfter(newBalance)
                .businessType("COURSE_PURCHASE")
                .businessId(order.getId())
                .remark(remark)
                .build();
        transactionMapper.insert(transaction);

        // 10. 添加用户课程关联
        UserCourse userCourse = UserCourse.builder()
                .userId(userId)
                .courseId(order.getCourseId())
                .sourceType(1) // 购买
                .orderId(order.getId())
                .isValid(1)
                .build();
        userCourseMapper.insert(userCourse);

        log.info("用户 {} 余额支付成功，订单号：{}", userId, orderNo);

        return Response.success("支付成功");
    }
}
