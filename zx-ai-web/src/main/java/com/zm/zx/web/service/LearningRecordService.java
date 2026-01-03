package com.zm.zx.web.service;

import com.zm.zx.web.domain.dto.SaveLearningRecordDTO;
import com.zm.zx.web.domain.vo.LearningRecordVO;

import java.util.List;

/**
 * 学习记录服务接口
 */
public interface LearningRecordService {
    
    /**
     * 保存或更新学习记录
     * @param userId 用户ID
     * @param dto 学习记录DTO
     * @return 是否成功
     */
    Boolean saveLearningRecord(Long userId, SaveLearningRecordDTO dto);
    
    /**
     * 获取用户的学习记录列表
     * @param userId 用户ID
     * @return 学习记录列表
     */
    List<LearningRecordVO> getLearningRecords(Long userId);
    
    /**
     * 获取用户在某个课程的最新学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 学习记录
     */
    LearningRecordVO getLearningRecordByCourse(Long userId, Long courseId);
    
    /**
     * 获取用户在某个章节的学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 学习记录
     */
    LearningRecordVO getLearningRecordByChapter(Long userId, Long courseId, Long chapterId);
}

