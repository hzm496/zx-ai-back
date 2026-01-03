package com.zm.zx.web.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web端 - 讲师VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebTeacherVO {
    
    /**
     * 讲师ID
     */
    private Long id;
    
    /**
     * 讲师姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 简介
     */
    private String intro;
    
    /**
     * 工作经历
     */
    private String experience;

}

