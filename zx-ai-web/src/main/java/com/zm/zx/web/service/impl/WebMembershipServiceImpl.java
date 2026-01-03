package com.zm.zx.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.constant.RedisKey;
import com.zm.zx.common.model.po.Membership;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.vo.MembershipVO;
import com.zm.zx.web.mapper.WebMembershipMapper;
import com.zm.zx.web.service.WebMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 会员配置 Service 实现类 (Web前台)
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WebMembershipServiceImpl implements WebMembershipService {

    private final WebMembershipMapper webMembershipMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 缓存过期时间：1小时
    private static final long CACHE_EXPIRE_HOURS = 1;

    @Override
    public Response getMembershipList() {
        // 1. 先从Redis缓存中获取
        try {
            Object cacheObj = redisTemplate.opsForValue().get(RedisKey.VIP_PACKAGE_INFO);
            if (cacheObj != null) {
                log.info("从Redis缓存获取会员配置列表");
                return Response.success(cacheObj);
            }
        } catch (Exception e) {
            log.error("从Redis获取会员配置失败", e);
        }
        
        // 2. 缓存未命中，从数据库查询
        log.info("Redis缓存未命中，从数据库查询会员配置列表");
        
        // 只查询启用的会员配置，按排序和ID排序
        LambdaQueryWrapper<Membership> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Membership::getStatus, 1)
                .orderByAsc(Membership::getSort, Membership::getId);
        
        List<Membership> membershipList = webMembershipMapper.selectList(queryWrapper);
        
        // 转换为VO
        List<MembershipVO> membershipVOList = membershipList.stream()
                .map(membership -> BeanUtil.copyProperties(membership, MembershipVO.class))
                .collect(Collectors.toList());
        
        // 3. 将结果存入Redis缓存
        try {
            redisTemplate.opsForValue().set(
                    RedisKey.VIP_PACKAGE_INFO, 
                    membershipVOList, 
                    CACHE_EXPIRE_HOURS, 
                    TimeUnit.HOURS
            );
            log.info("会员配置列表已缓存到Redis，过期时间：{}小时", CACHE_EXPIRE_HOURS);
        } catch (Exception e) {
            log.error("缓存会员配置到Redis失败", e);
        }
        
        return Response.success(membershipVOList);
    }
}

