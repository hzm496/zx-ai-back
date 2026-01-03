package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.activity.po.Activity;
import com.zm.zx.web.model.vo.ActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动 Mapper（Web用户端）
 */
@Mapper
public interface WebActivityMapper extends BaseMapper<Activity> {

    /**
     * 查询可领取的活动列表
     */
    List<ActivityVO> findAvailableActivities(@Param("userId") Long userId);
    
    /**
     * 查询活动详情（包含奖励信息和用户领取状态）
     */
    ActivityVO findActivityDetail(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 扣减活动库存（带库存校验，防止超卖）
     * @param activityPO 活动对象（需要包含id）
     * @return 影响行数，1表示成功，0表示库存不足或活动已结束
     */
    int updateByActivityId(Activity activityPO);
}
