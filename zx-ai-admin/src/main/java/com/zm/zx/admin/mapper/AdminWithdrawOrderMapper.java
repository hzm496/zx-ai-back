package com.zm.zx.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.admin.model.vo.WithdrawOrderVO;
import com.zm.zx.common.model.withdraw.po.WithdrawOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 提现订单Mapper（后台管理）
 */
@Mapper
public interface AdminWithdrawOrderMapper extends BaseMapper<WithdrawOrder> {
    
    /**
     * 分页查询提现订单列表（关联用户表）
     * @param page 分页参数
     * @param withdrawNo 流水号
     * @param userId 用户ID
     * @param status 状态
     * @return 提现订单列表
     */
    IPage<WithdrawOrderVO> selectWithdrawListWithUser(
            Page<?> page,
            @Param("withdrawNo") String withdrawNo,
            @Param("userId") Long userId,
            @Param("status") Integer status
    );
    
    /**
     * 根据ID查询提现订单详情（关联用户表）
     * @param id 提现订单ID
     * @return 提现订单详情
     */
    WithdrawOrderVO selectWithdrawByIdWithUser(@Param("id") Long id);
}

