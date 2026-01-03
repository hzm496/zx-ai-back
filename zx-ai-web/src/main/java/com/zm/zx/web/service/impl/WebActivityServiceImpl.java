package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zm.zx.common.constant.RedisKey;
import com.zm.zx.common.enums.LuaResEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.mapper.ActivityRewardMapper;
import com.zm.zx.web.mapper.WebUserActivityRecordMapper;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.web.mapper.*;
import com.zm.zx.common.model.activity.po.Activity;
import com.zm.zx.common.model.activity.po.ActivityReward;
import com.zm.zx.common.model.record.po.UserActivityRecord;
import com.zm.zx.web.model.po.Coupon;
import com.zm.zx.web.model.po.UserCoupon;
import com.zm.zx.web.model.vo.ActivityVO;
import com.zm.zx.web.service.WebActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.zm.zx.common.constant.RedisKey.*;

/**
 * 活动服务实现类（用户端）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebActivityServiceImpl implements WebActivityService {

    private final WebActivityMapper activityMapper;
    private final ActivityRewardMapper activityRewardMapper;
    private final WebUserActivityRecordMapper userActivityRecordMapper;
    private final UserMapper userMapper;
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response getAvailableActivities() {
        Long userId = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsLong();
        }

        log.info("查询可领取的活动列表，用户ID：{}", userId);

        List<ActivityVO> activities = activityMapper.findAvailableActivities(userId);

        // 判断用户是否可以领取
        if (userId != null) {
            for (ActivityVO activity : activities) {
                boolean canReceive = checkCanReceive(userId, activity);
                activity.setCanReceive(canReceive);
                activity.setHasReceived(activity.getUserReceiveCount() != null && activity.getUserReceiveCount() > 0);
            }
        }

        return Response.success(activities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response receiveActivity(Long activityId) {
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 领取活动 {}", userId, activityId);
        // 查询活动详情
        ActivityVO activity = activityMapper.findActivityDetail(activityId, userId);
        if (activity == null) {
            throw new BizException("活动不存在");
        }

        // 验证活动是否可领取
        if (!checkCanReceive(userId, activity)) {
            throw new BizException("该活动暂时无法领取");
        }

        // 查询奖励配置
        ActivityReward reward = activityRewardMapper.selectOne(
                new LambdaQueryWrapper<ActivityReward>()
                        .eq(ActivityReward::getActivityId, activityId)
        );

        if (reward == null) {
            throw new BizException("活动奖励配置不存在");
        }

        String secKillKey = RedisKey.buildSecKillKey(userId, activityId);
        RLock lock = redissonClient.getLock(secKillKey);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            return Response.fail("请稍后再试");
        }
        Long res = null;
        String activityStockKey = buildActivityStockKey(activityId);
        String activityUserIdSetKey = buildActivityUserIdSetKey(activityId);
        try {
            //执行脚本
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setResultType(Long.class);
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/check_and_sekill.lua")));
            res = redisTemplate.execute(script, List.of(activityUserIdSetKey, activityStockKey), userId);
        } catch (Exception e) {
            log.error("领取失败", e);
        } finally {
            lock.unlock();
        }
        LuaResEnum luaResEnum = LuaResEnum.getByCode(res);
        log.info("Lua脚本执行结果：{}, 返回值：{}", luaResEnum, res);

        if (luaResEnum == null) {
            throw new BizException("领取失败，未知错误");
        }

        switch (luaResEnum) {
            case ALREADY_PURCHASED:
                return Response.fail("您已领取过该活动");
            case NOT_ENOUGH_STOCK:
                return Response.fail("活动名额已被抢光");
            case SUCCESS:
                log.info("秒杀成功，开始发放奖励");
                break;
        }

        // 根据奖励类型发放奖励
        Long couponId = null;
        if (reward.getRewardType() == 1) {
            // 送会员
            grantVipMembership(userId, reward.getVipDuration());
        } else if (reward.getRewardType() == 2) {
            // 送优惠券
            couponId = grantCoupon(userId, reward, activity.getTitle());
        }

        // 更新活动领取次数
        Activity activityPO = activityMapper.selectById(activityId);
//        activityPO.setReceiveCount(activityPO.getReceiveCount() + 1);
        activityMapper.updateByActivityId(activityPO);
//        activityMapper.updateById(activityPO);

        // 记录领取记录
        UserActivityRecord record = UserActivityRecord.builder()
                .userId(userId)
                .activityId(activityId)
                .rewardType(reward.getRewardType())
                .vipDuration(reward.getVipDuration())
                .couponId(couponId)
                .receiveTime(LocalDateTime.now())
                .build();
        userActivityRecordMapper.insert(record);

        String message = reward.getRewardType() == 1
                ? "恭喜您获得" + getVipDurationName(reward.getVipDuration()) + "会员！"
                : "恭喜您获得" + reward.getCouponAmount() + "元优惠券！";

        log.info("用户 {} 成功领取活动 {}，奖励类型：{}", userId, activityId, reward.getRewardType());
        return Response.success(message);
    }

    /**
     * 检查用户是否可以领取活动
     */
    private boolean checkCanReceive(Long userId, ActivityVO activity) {
        // 检查活动时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            return false;
        }

        // 检查总限额
        if (activity.getTotalLimit() > 0 && activity.getReceiveCount() >= activity.getTotalLimit()) {
            return false;
        }

        // 检查用户领取次数
        int userReceiveCount = activity.getUserReceiveCount() != null ? activity.getUserReceiveCount() : 0;
        if (activity.getLimitPerUser() > 0 && userReceiveCount >= activity.getLimitPerUser()) {
            return false;
        }

        return true;
    }

    /**
     * 发放VIP会员
     */
    private void grantVipMembership(Long userId, Integer duration) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpireTime;

        if (user.getIsVip() == 1 && user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(now)) {
            // 已是VIP且未过期，续期
            newExpireTime = user.getVipExpireTime().plusDays(duration);
        } else {
            // 新开通或已过期
            newExpireTime = now.plusDays(duration);
        }

        user.setIsVip(1);
        user.setVipExpireTime(newExpireTime);
        userMapper.updateById(user);

        log.info("用户 {} 获得VIP会员 {} 天，到期时间：{}", userId, duration, newExpireTime);
    }

    /**
     * 发放优惠券
     */
    private Long grantCoupon(Long userId, ActivityReward reward, String activityTitle) {
        // 创建优惠券
        Coupon coupon = Coupon.builder()
                .name(activityTitle + " - 专属优惠券")
                .type(1) // 满减券
                .discountAmount(reward.getCouponAmount())
                .minAmount(reward.getCouponMinAmount() != null ? reward.getCouponMinAmount() : reward.getCouponAmount())
                .totalCount(0) // 活动券不限量
                .receivedCount(0)
                .usedCount(0)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusYears(10)) // 券本身10年有效
                .validDays(reward.getCouponExpireDays()) // 用户领取后的有效天数
                .status(1)
                .description("购买课程满" + reward.getCouponMinAmount() + "元可用")
                .build();

        couponMapper.insert(coupon);

        // 给用户发放优惠券
        LocalDateTime expireTime = LocalDateTime.now().plusDays(reward.getCouponExpireDays());
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(coupon.getId())
                .status(0) // 未使用
                .receiveTime(LocalDateTime.now())
                .expireTime(expireTime)
                .build();

        userCouponMapper.insert(userCoupon);

        log.info("用户 {} 获得优惠券，金额：{}，有效期至：{}", userId, reward.getCouponAmount(), expireTime);
        return userCoupon.getId();
    }

    /**
     * 获取VIP时长名称
     */
    private String getVipDurationName(Integer duration) {
        if (duration == 1) return "体验卡";
        if (duration == 30) return "月卡";
        if (duration == 90) return "季卡";
        if (duration == 365) return "年卡";
        return duration + "天";
    }
}
