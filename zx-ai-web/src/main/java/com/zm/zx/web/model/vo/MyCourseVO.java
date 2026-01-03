package com.zm.zx.web.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 我的课程 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyCourseVO {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程封面
     */
    private String cover;

    /**
     * 课程价格
     */
    private BigDecimal price;

    /**
     * 讲师ID
     */
    private Long teacherId;

    /**
     * 讲师名称
     */
    private String teacherName;

    /**
     * 难度：1-入门，2-初级，3-中级，4-高级
     */
    private Integer difficulty;

    /**
     * 难度名称
     */
    private String difficultyName;

    /**
     * 总时长（分钟）
     */
    private Integer duration;

    /**
     * 获取方式：1-购买，2-VIP免费，3-赠送，4-兑换
     */
    private Integer sourceType;

    /**
     * 获取方式名称
     */
    private String sourceTypeName;

    /**
     * 获取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime obtainTime;

    /**
     * 学习进度（百分比）
     */
    private Integer progress;
}

