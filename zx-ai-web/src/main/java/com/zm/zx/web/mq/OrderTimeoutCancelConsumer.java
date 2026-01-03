package com.zm.zx.web.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.constant.MQConstants;
import com.zm.zx.web.mapper.CourseOrderMapper;
import com.zm.zx.web.mapper.UserCouponMapper;
import com.zm.zx.web.model.po.CourseOrder;
import com.zm.zx.web.model.po.UserCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 订单超时取消消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_ORDER_TIMEOUT_CANCEL,
        consumerGroup = "ORDER_TIMEOUT_CANCEL_CONSUMER_GROUP"
)
public class OrderTimeoutCancelConsumer implements RocketMQListener<String> {

    private final CourseOrderMapper courseOrderMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String orderNo) {
        log.info("收到订单超时取消消息，订单号：{}", orderNo);

        try {
            // 1. 查询订单
            LambdaQueryWrapper<CourseOrder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CourseOrder::getOrderNo, orderNo);
            CourseOrder order = courseOrderMapper.selectOne(queryWrapper);

            if (order == null) {
                log.warn("订单不存在，订单号：{}", orderNo);
                return;
            }

            // 2. 如果订单状态为待支付（status=0），则取消订单
            if (order.getStatus() == 0) {
                order.setStatus(2); // 2-已取消
                order.setCancelTime(LocalDateTime.now());
                order.setUpdateTime(LocalDateTime.now());
                courseOrderMapper.updateById(order);

                // 3. 如果使用了优惠券，释放优惠券
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

                log.info("订单超时自动取消成功，订单号：{}", orderNo);
            } else {
                log.info("订单已支付或已取消，无需处理，订单号：{}，订单状态：{}", orderNo, order.getStatus());
            }
        } catch (Exception e) {
            log.error("处理订单超时取消消息失败，订单号：{}", orderNo, e);
            throw e; // 抛出异常，触发重试
        }
    }
}

