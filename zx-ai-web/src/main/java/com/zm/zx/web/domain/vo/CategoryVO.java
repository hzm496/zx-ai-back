package com.zm.zx.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Web - 课程分类 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryVO {

    private Long id;
    private String name;
    private Long parentId;
    private String cover;
    private String icon;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 课程数量（仅在热门分类接口返回）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer courseCount;
}

