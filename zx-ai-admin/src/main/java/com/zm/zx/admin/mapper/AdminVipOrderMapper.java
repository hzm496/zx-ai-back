package com.zm.zx.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.admin.model.dto.FindVipOrderListDTO;
import com.zm.zx.admin.model.vo.VipOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * VIP订单 Mapper（管理后台）
 */
@Mapper
public interface AdminVipOrderMapper {
    
    /**
     * 查询VIP订单列表（分页）
     */
    IPage<VipOrderVO> selectVipOrderList(Page<VipOrderVO> page, @Param("dto") FindVipOrderListDTO findVipOrderListDTO);
    
    /**
     * 根据ID查询VIP订单详情
     */
    VipOrderVO selectVipOrderById(@Param("id") Long id);
    
    /**
     * 删除VIP订单
     */
    int deleteVipOrderById(@Param("id") Long id);
}

