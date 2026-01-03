package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.mapper.MembershipMapper;
import com.zm.zx.admin.model.dto.MembershipAddDTO;
import com.zm.zx.admin.model.dto.MembershipUpdateDTO;
import com.zm.zx.common.model.po.Membership;
import com.zm.zx.admin.model.vo.MembershipVO;
import com.zm.zx.admin.service.MembershipService;
import com.zm.zx.common.constant.RedisKey;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员配置 Service 实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MembershipServiceImpl extends ServiceImpl<MembershipMapper, Membership> implements MembershipService {

    private final MembershipMapper membershipMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response getMembershipList() {
        // 查询所有会员配置，按排序和ID排序
        LambdaQueryWrapper<Membership> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Membership::getSort, Membership::getId);
        
        List<Membership> membershipList = membershipMapper.selectList(queryWrapper);
        
        // 转换为VO
        List<MembershipVO> membershipVOList = membershipList.stream()
                .map(membership -> BeanUtil.copyProperties(membership, MembershipVO.class))
                .collect(Collectors.toList());
        
        return Response.success(membershipVOList);
    }

    @Override
    public Response addMembership(MembershipAddDTO membershipAddDTO) {
        // 创建会员配置
        Membership membership = BeanUtil.copyProperties(membershipAddDTO, Membership.class);
        
        // 如果状态为空，默认为启用
        if (membership.getStatus() == null) {
            membership.setStatus(1);
        }
        
        // 保存
        boolean saved = this.save(membership);
        if (!saved) {
            throw new BizException(ResponseEnum.ADD_FAIL);
        }
        
        // 清除Redis缓存
        clearMembershipCache();
        
        log.info("添加会员配置成功，ID：{}，清除Redis缓存", membership.getId());
        
        return Response.success();
    }

    @Override
    public Response updateMembership(MembershipUpdateDTO membershipUpdateDTO) {
        // 检查会员配置是否存在
        Membership membership = this.getById(membershipUpdateDTO.getId());
        if (membership == null) {
            throw new BizException("会员配置不存在");
        }
        
        // 更新会员配置
        Membership updateMembership = BeanUtil.copyProperties(membershipUpdateDTO, Membership.class);
        
        boolean updated = this.updateById(updateMembership);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }
        
        // 清除Redis缓存
        clearMembershipCache();
        
        log.info("更新会员配置成功，ID：{}，清除Redis缓存", membership.getId());
        
        return Response.success();
    }

    @Override
    public Response deleteMembership(Long id) {
        // 检查会员配置是否存在
        Membership membership = this.getById(id);
        if (membership == null) {
            throw new BizException("会员配置不存在");
        }
        
        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }
        
        // 清除Redis缓存
        clearMembershipCache();
        
        log.info("删除会员配置成功，ID：{}，清除Redis缓存", id);
        
        return Response.success();
    }

    @Override
    public Response getMembershipById(Long id) {
        Membership membership = this.getById(id);
        if (membership == null) {
            throw new BizException("会员配置不存在");
        }
        
        MembershipVO membershipVO = BeanUtil.copyProperties(membership, MembershipVO.class);
        return Response.success(membershipVO);
    }
    
    /**
     * 清除会员配置缓存
     */
    private void clearMembershipCache() {
        try {
            redisTemplate.delete(RedisKey.VIP_PACKAGE_INFO);
            log.info("清除会员配置缓存成功，key: {}", RedisKey.VIP_PACKAGE_INFO);
        } catch (Exception e) {
            log.error("清除会员配置缓存失败", e);
        }
    }
}

