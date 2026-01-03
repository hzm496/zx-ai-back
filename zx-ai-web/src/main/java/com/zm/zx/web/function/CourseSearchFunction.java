package com.zm.zx.web.function;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.mapper.CourseCategoryMapper;
import com.zm.zx.common.mapper.CourseMapper;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.common.model.course.po.CourseCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 课程搜索Function - 供AI调用查询数据库中真实存在的课程
 */
@Slf4j
@Component
@Description("搜索平台上的课程信息，可以根据关键词、分类或难度等级查询课程")
@RequiredArgsConstructor
public class CourseSearchFunction implements Function<CourseSearchFunction.Request, CourseSearchFunction.Response> {

    private final CourseMapper courseMapper;
    private final CourseCategoryMapper categoryMapper;

    /**
     * 请求参数
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class Request {
        /** 搜索关键词，用于匹配课程标题 */
        public String keyword;
        
        /** 课程分类，如：Java、Python、前端、后端等 */
        public String category;
        
        /** 最多返回的课程数量，默认5条，最多20条 */
        public Integer limit;
    }

    /**
     * 响应结果
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class Response {
        public List<CourseInfo> courses;
    }

    /**
     * 课程信息
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class CourseInfo {
        public Long id;
        public String title;
        public String category;
        public String difficulty;
        public String price;
        public Integer buyCount;
        public String description;
    }

    @Override
    public Response apply(Request request) {
        log.info("AI调用CourseSearchFunction - 关键词:{}, 分类:{}, 数量:{}", 
                request.getKeyword(), request.getCategory(), request.getLimit());

        // 构建查询条件
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1); // 只查询上架的课程

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            wrapper.like(Course::getTitle, request.getKeyword());
        }

        // 分类搜索
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            // 先查询分类ID
            LambdaQueryWrapper<CourseCategory> categoryWrapper = new LambdaQueryWrapper<>();
            categoryWrapper.like(CourseCategory::getName, request.getCategory());
            List<CourseCategory> categories = categoryMapper.selectList(categoryWrapper);
            
            if (!categories.isEmpty()) {
                List<Long> categoryIds = categories.stream()
                        .map(CourseCategory::getId)
                        .collect(Collectors.toList());
                wrapper.in(Course::getCategoryId, categoryIds);
            }
        }

        // 按购买人数排序
        wrapper.orderByDesc(Course::getBuyCount);
        wrapper.orderByDesc(Course::getViewCount);

        // 限制数量
        int limit = request.getLimit() != null ? Math.min(request.getLimit(), 20) : 5;
        wrapper.last("LIMIT " + limit);

        List<Course> courses = courseMapper.selectList(wrapper);

        // 查询课程分类名称
        List<Long> categoryIds = courses.stream()
                .map(Course::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        List<CourseCategory> categories = categoryIds.isEmpty() ? 
                List.of() : categoryMapper.selectList(
                        new LambdaQueryWrapper<CourseCategory>().in(CourseCategory::getId, categoryIds));

        // 转换为CourseInfo
        List<CourseInfo> courseInfos = courses.stream()
                .map(course -> {
                    String categoryName = categories.stream()
                            .filter(c -> c.getId().equals(course.getCategoryId()))
                            .findFirst()
                            .map(CourseCategory::getName)
                            .orElse("未分类");

                    String price = course.getIsFree() == 1 ? "免费" : 
                            (course.getPrice() != null ? "¥" + course.getPrice() : "未定价");

                    String difficulty = switch (course.getDifficulty()) {
                        case 1 -> "入门";
                        case 2 -> "初级";
                        case 3 -> "中级";
                        case 4 -> "高级";
                        default -> "未知";
                    };

                    return new CourseInfo(
                            course.getId(),
                            course.getTitle(),
                            categoryName,
                            difficulty,
                            price,
                            course.getBuyCount(),
                            course.getDescription()
                    );
                })
                .collect(Collectors.toList());

        log.info("查询到{}门课程", courseInfos.size());
        return new Response(courseInfos);
    }
}

