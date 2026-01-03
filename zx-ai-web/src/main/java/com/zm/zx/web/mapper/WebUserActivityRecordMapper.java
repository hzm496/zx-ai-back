package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.common.model.record.po.UserActivityRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户活动领取记录 Mapper
 */
@Mapper
public interface WebUserActivityRecordMapper extends BaseMapper<UserActivityRecord> {
    
    /**
     * 统计用户领取某活动的次数
     */
    int countUserActivityReceive(@Param("userId") Long userId, @Param("activityId") Long activityId);
}
