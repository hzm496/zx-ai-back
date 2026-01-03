package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.domain.po.LearningRecord;
import com.zm.zx.web.domain.vo.LearningRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习记录 Mapper
 */
@Mapper
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {
    
    /**
     * 查询用户的学习记录列表（带课程和章节信息）
     * @param userId 用户ID
     * @return 学习记录列表
     */
    List<LearningRecordVO> selectLearningRecordsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户在某个课程的学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 学习记录
     */
    LearningRecordVO selectLearningRecordByCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    /**
     * 插入或更新学习记录（使用 INSERT ... ON DUPLICATE KEY UPDATE）
     * 基于唯一索引 uk_user_course(user_id, course_id)
     * 每个用户的每个课程只保存一条学习记录（记录最后学习的章节）
     *
     * @param record 学习记录
     * @return 影响行数
     */
    boolean insertOrUpdate(LearningRecord record);
}

