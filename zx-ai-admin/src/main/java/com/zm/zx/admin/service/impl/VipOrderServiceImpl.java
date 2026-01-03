package com.zm.zx.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.admin.mapper.AdminVipOrderMapper;
import com.zm.zx.admin.model.dto.FindVipOrderListDTO;
import com.zm.zx.admin.model.vo.VipOrderVO;
import com.zm.zx.admin.service.VipOrderService;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * VIP订单 Service 实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VipOrderServiceImpl implements VipOrderService {
    
    private final AdminVipOrderMapper adminVipOrderMapper;
    
    /**
     * 查询VIP订单列表（分页）
     */
    @Override
    public PageResponse findVipOrderList(FindVipOrderListDTO findVipOrderListDTO) {
        // 设置分页参数
        Integer pageNum = findVipOrderListDTO.getPageNum();
        Integer pageSize = findVipOrderListDTO.getPageSize();
        
        Page<VipOrderVO> page = new Page<>(pageNum, pageSize);
        IPage<VipOrderVO> vipOrderPage = adminVipOrderMapper.selectVipOrderList(page, findVipOrderListDTO);
        
        List<VipOrderVO> records = vipOrderPage.getRecords();
        
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }
        
        return PageResponse.success(records, pageNum, vipOrderPage.getTotal(), pageSize);
    }
    
    /**
     * 根据ID获取VIP订单详情
     */
    @Override
    public Response getVipOrderById(Long id) {
        VipOrderVO vipOrder = adminVipOrderMapper.selectVipOrderById(id);
        if (vipOrder == null) {
            return Response.fail("VIP订单不存在");
        }
        return Response.success(vipOrder);
    }
    
    /**
     * 删除VIP订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteVipOrder(Long id) {
        // 检查订单是否存在
        VipOrderVO vipOrder = adminVipOrderMapper.selectVipOrderById(id);
        if (vipOrder == null) {
            return Response.fail("VIP订单不存在");
        }
        
        // 删除订单
        int result = adminVipOrderMapper.deleteVipOrderById(id);
        if (result > 0) {
            log.info("删除VIP订单成功, id: {}", id);
            return Response.success("删除成功");
        } else {
            log.error("删除VIP订单失败, id: {}", id);
            return Response.fail("删除失败");
        }
    }
}

