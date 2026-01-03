package com.zm.zx.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.admin.mapper.AdminActivityMapper;
import com.zm.zx.common.mapper.ActivityRewardMapper;
import com.zm.zx.admin.model.dto.ActivityAddDTO;
import com.zm.zx.admin.model.dto.ActivityUpdateDTO;
import com.zm.zx.admin.model.dto.FindActivityListDTO;
import com.zm.zx.common.model.activity.po.Activity;
import com.zm.zx.common.model.activity.po.ActivityReward;
import com.zm.zx.admin.model.vo.ActivityVO;
import com.zm.zx.admin.service.AdminActivityService;
import com.zm.zx.common.constant.RedisKey;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 活动服务实现类（管理端）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminActivityServiceImpl implements AdminActivityService {

    private final AdminActivityMapper activityMapper;
    private final ActivityRewardMapper activityRewardMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResponse findActivityList(FindActivityListDTO dto) {
        log.info("分页查询活动列表，参数：{}", dto);

        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();

        Page<ActivityVO> page = new Page<>(pageNum, pageSize);
        IPage<ActivityVO> activityPage = activityMapper.findActivityList(page, dto);

        List<ActivityVO> records = activityPage.getRecords();

        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }

        return PageResponse.success(records, pageNum, activityPage.getTotal(), pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addActivity(ActivityAddDTO dto) {
        log.info("添加活动，参数：{}", dto);

        // 验证时间
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BizException("结束时间不能早于开始时间");
        }

        // 验证奖励配置
        if (dto.getType() == 1) {
            // 送会员
            if (dto.getVipDuration() == null || dto.getVipDuration() <= 0) {
                throw new BizException("请设置VIP时长");
            }
        } else if (dto.getType() == 2) {
            // 送优惠券
            if (dto.getCouponAmount() == null || dto.getCouponAmount().doubleValue() <= 0) {
                throw new BizException("请设置优惠券金额");
            }
            if (dto.getCouponExpireDays() == null || dto.getCouponExpireDays() <= 0) {
                throw new BizException("请设置优惠券有效期");
            }
        }

        // 创建活动
        Activity activity = Activity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(dto.getType())
                .coverImage(dto.getCoverImage())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .limitPerUser(dto.getLimitPerUser() != null ? dto.getLimitPerUser() : 1)
                .totalLimit(dto.getTotalLimit() != null ? dto.getTotalLimit() : 0)
                .receiveCount(0)
                .status(1)
                .build();

        activityMapper.insert(activity);

        // 创建奖励配置
        ActivityReward reward = ActivityReward.builder()
                .activityId(activity.getId())
                .rewardType(dto.getType())
                .build();

        if (dto.getType() == 1) {
            // 送会员
            reward.setVipDuration(dto.getVipDuration());
        } else if (dto.getType() == 2) {
            // 送优惠券
            reward.setCouponAmount(dto.getCouponAmount());
            reward.setCouponMinAmount(dto.getCouponMinAmount());
            reward.setCouponExpireDays(dto.getCouponExpireDays());
        }

        activityRewardMapper.insert(reward);

        // 初始化Redis库存（用于秒杀）
        if (activity.getTotalLimit() > 0) {
            String activityStockKey = RedisKey.buildActivityStockKey(activity.getId());
            redisTemplate.opsForValue().set(activityStockKey, activity.getTotalLimit());
            log.info("初始化活动库存到Redis，活动ID：{}，库存：{}", activity.getId(), activity.getTotalLimit());
        }

        log.info("活动添加成功，活动ID：{}", activity.getId());
        return Response.success("活动创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateActivity(ActivityUpdateDTO dto) {
        log.info("更新活动，参数：{}", dto);

        // 查询活动
        Activity activity = activityMapper.selectById(dto.getId());
        if (activity == null) {
            throw new BizException("活动不存在");
        }

        // 更新活动
        if (dto.getTitle() != null) {
            activity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            activity.setDescription(dto.getDescription());
        }
        if (dto.getCoverImage() != null) {
            activity.setCoverImage(dto.getCoverImage());
        }
        if (dto.getStartTime() != null) {
            activity.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            activity.setEndTime(dto.getEndTime());
        }
        if (dto.getLimitPerUser() != null) {
            activity.setLimitPerUser(dto.getLimitPerUser());
        }
        if (dto.getTotalLimit() != null) {
            activity.setTotalLimit(dto.getTotalLimit());
        }
        if (dto.getStatus() != null) {
            activity.setStatus(dto.getStatus());
        }

        // 验证时间
        if (activity.getEndTime().isBefore(activity.getStartTime())) {
            throw new BizException("结束时间不能早于开始时间");
        }

        activityMapper.updateById(activity);

        // 更新奖励配置
        ActivityReward reward = activityRewardMapper.selectOne(
                new LambdaQueryWrapper<ActivityReward>()
                        .eq(ActivityReward::getActivityId, dto.getId())
        );

        if (reward != null) {
            if (activity.getType() == 1 && dto.getVipDuration() != null) {
                reward.setVipDuration(dto.getVipDuration());
            } else if (activity.getType() == 2) {
                if (dto.getCouponAmount() != null) {
                    reward.setCouponAmount(dto.getCouponAmount());
                }
                if (dto.getCouponMinAmount() != null) {
                    reward.setCouponMinAmount(dto.getCouponMinAmount());
                }
                if (dto.getCouponExpireDays() != null) {
                    reward.setCouponExpireDays(dto.getCouponExpireDays());
                }
            }
            activityRewardMapper.updateById(reward);
        }

        log.info("活动更新成功，活动ID：{}", dto.getId());
        return Response.success("活动更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteActivity(Long id) {
        log.info("删除活动，活动ID：{}", id);

        // 删除奖励配置
        activityRewardMapper.delete(
                new LambdaQueryWrapper<ActivityReward>()
                        .eq(ActivityReward::getActivityId, id)
        );

        // 删除活动
        activityMapper.deleteById(id);

        log.info("活动删除成功，活动ID：{}", id);
        return Response.success("活动删除成功");
    }

    @Override
    public Response updateActivityStatus(Long id, Integer status) {
        log.info("更新活动状态，活动ID：{}，状态：{}", id, status);

        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BizException("活动不存在");
        }

        activity.setStatus(status);
        activityMapper.updateById(activity);

        log.info("活动状态更新成功");
        return Response.success("状态更新成功");
    }
}
