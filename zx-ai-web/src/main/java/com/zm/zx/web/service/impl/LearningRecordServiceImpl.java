package com.zm.zx.web.service.impl;

import com.zm.zx.web.domain.dto.SaveLearningRecordDTO;
import com.zm.zx.web.domain.po.LearningRecord;
import com.zm.zx.web.domain.vo.LearningRecordVO;
import com.zm.zx.web.mapper.LearningRecordMapper;
import com.zm.zx.web.service.LearningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningRecordServiceImpl implements LearningRecordService {
    
    private final LearningRecordMapper learningRecordMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveLearningRecord(Long userId, SaveLearningRecordDTO dto) {
        // 计算是否已完成（观看进度>=90%视为完成）
        int isFinished = (dto.getDuration() > 0 && (double) dto.getProgress() / dto.getDuration() >= 0.9) ? 1 : 0;
        
        // 构建学习记录
        LearningRecord record = LearningRecord.builder()
            .userId(userId)
            .courseId(dto.getCourseId())
            .chapterId(dto.getChapterId())
            .progress(dto.getProgress())
            .duration(dto.getDuration())
            .isFinished(isFinished)
            .lastLearnTime(LocalDateTime.now())
            .build();
        
        // 使用 INSERT ... ON DUPLICATE KEY UPDATE
        // 基于唯一索引 uk_user_course(user_id, course_id)
        // 如果该用户该课程的记录已存在则更新（包括章节ID），否则插入
        return learningRecordMapper.insertOrUpdate(record);
    }
    
    @Override
    public List<LearningRecordVO> getLearningRecords(Long userId) {
        return learningRecordMapper.selectLearningRecordsByUserId(userId);
    }
    
    @Override
    public LearningRecordVO getLearningRecordByCourse(Long userId, Long courseId) {
        return learningRecordMapper.selectLearningRecordByCourseId(userId, courseId);
    }
    
    @Override
    public LearningRecordVO getLearningRecordByChapter(Long userId, Long courseId, Long chapterId) {
        // 由于每个课程只保存一条学习记录，直接返回该课程的学习记录即可
        return learningRecordMapper.selectLearningRecordByCourseId(userId, courseId);
    }
}

