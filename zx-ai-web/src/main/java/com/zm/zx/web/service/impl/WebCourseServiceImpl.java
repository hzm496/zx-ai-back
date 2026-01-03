package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.mapper.CourseCategoryMapper;
import com.zm.zx.common.mapper.CourseChapterMapper;
import com.zm.zx.common.mapper.CourseMapper;
import com.zm.zx.common.model.teacher.po.Teacher;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.po.Comment;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.common.model.course.po.CourseCategory;
import com.zm.zx.common.model.course.po.CourseChapter;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.web.domain.vo.ChapterTreeVO;
import com.zm.zx.web.domain.vo.CourseCategoryVO;
import com.zm.zx.web.domain.vo.CourseDetailVO;
import com.zm.zx.web.domain.vo.WebCourseVO;
import com.zm.zx.web.enums.StatusEnum;
import com.zm.zx.web.mapper.*;
import com.zm.zx.web.service.WebCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Web端 - 课程Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebCourseServiceImpl implements WebCourseService {

    private final CourseMapper courseMapper;
    private final CourseCategoryMapper categoryMapper;
    private final WebTeacherMapper teacherMapper;
    private final CourseChapterMapper courseChapterMapper;
    private final UserMapper userMapper;
    private final CourseCategoryMapper courseCategoryMapper;
    private final CommentMapper commentMapper;
    private final CourseOrderMapper courseOrderMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response getCoursesByTeacherId(Long teacherId) {
        // 查询该讲师的所有上架课程
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getTeacherId, teacherId)
                .eq(Course::getStatus, 1) // 只查询上架状态
                .orderByDesc(Course::getCreateTime);

        List<Course> courses = courseMapper.selectList(queryWrapper);

        // 获取所有分类ID和讲师ID
        List<Long> categoryIds = courses.stream()
                .map(Course::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询分类信息
        Map<Long, String> categoryMap;
        if (categoryIds.isEmpty()) {
            categoryMap = Map.of();
        } else {
            LambdaQueryWrapper<CourseCategory> categoryQueryWrapper = new LambdaQueryWrapper<>();
            categoryQueryWrapper.in(CourseCategory::getId, categoryIds);
            categoryMap = categoryMapper.selectList(categoryQueryWrapper).stream()
                    .collect(Collectors.toMap(CourseCategory::getId, CourseCategory::getName));
        }

        // 查询讲师信息
        Teacher teacher = teacherMapper.selectById(teacherId);
        String teacherName = teacher != null ? teacher.getName() : "";
        String teacherAvatar = teacher != null ? teacher.getAvatar() : "";

        // 转换为VO
        List<WebCourseVO> courseVOList = courses.stream()
                .map(course -> convertToVO(course, categoryMap, teacherName, teacherAvatar))
                .collect(Collectors.toList());

        return Response.success(courseVOList);
    }

    @Override
    public Response getRecommendCourses() {
        // 查询推荐课程：按购买人数排序，取前6门上架课程
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getStatus, 1) // 只查询上架状态
                .orderByDesc(Course::getBuyCount) // 按购买人数降序
                .orderByDesc(Course::getViewCount) // 浏览量降序
                .last("LIMIT 6"); // 限制6条

        List<Course> courses = courseMapper.selectList(queryWrapper);

        // 获取所有分类ID和讲师ID
        List<Long> categoryIds = courses.stream()
                .map(Course::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> teacherIds = courses.stream()
                .map(Course::getTeacherId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询分类信息
        Map<Long, String> categoryMap;
        if (!categoryIds.isEmpty()) {
            List<CourseCategory> categories = categoryMapper.selectList(
                    new LambdaQueryWrapper<CourseCategory>()
                            .in(CourseCategory::getId, categoryIds)
            );
            categoryMap = categories.stream()
                    .collect(Collectors.toMap(CourseCategory::getId, CourseCategory::getName));
        } else {
            categoryMap = Map.of();
        }

        // 批量查询讲师信息
        Map<Long, Teacher> teacherMap;
        if (!teacherIds.isEmpty()) {
            List<Teacher> teachers = teacherMapper.selectList(
                    new LambdaQueryWrapper<Teacher>()
                            .in(Teacher::getId, teacherIds)
            );
            teacherMap = teachers.stream()
                    .collect(Collectors.toMap(Teacher::getId, teacher -> teacher));
        } else {
            teacherMap = Map.of();
        }

        // 转换为VO
        List<WebCourseVO> courseVOList = courses.stream()
                .map(course -> {
                    Teacher teacher = teacherMap.get(course.getTeacherId());
                    String teacherName = teacher != null ? teacher.getName() : "";
                    String teacherAvatar = teacher != null ? teacher.getAvatar() : "";
                    return convertToVO(course, categoryMap, teacherName, teacherAvatar);
                })
                .collect(Collectors.toList());

        log.info("查询推荐课程成功，共{}门", courseVOList.size());
        return Response.success(courseVOList);
    }

    /**
     * 将Course PO 转换为 VO
     */
    @Override
    public Response getCoursesByCategory(Long categoryId, Integer pageNo, Integer pageSize, Integer isFree) {
        log.info("分页查询分类课程，categoryId: {}, pageNo: {}, pageSize: {}, isFree: {}", categoryId, pageNo, pageSize, isFree);

        // 创建分页对象
        Page<Course> page = new Page<>(pageNo, pageSize);

        // 查询该分类下所有上架状态的课程
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getCategoryId, categoryId)
                .eq(Course::getStatus, 1);
        
        // 如果指定了isFree参数，添加筛选条件
        if (isFree != null) {
            queryWrapper.eq(Course::getIsFree, isFree);
        }
        
        queryWrapper.orderByDesc(Course::getSort)
                .orderByDesc(Course::getCreateTime);

        Page<Course> coursePage = courseMapper.selectPage(page, queryWrapper);
        List<Course> courses = coursePage.getRecords();

        if (courses.isEmpty()) {
            log.info("分类{}下没有课程", categoryId);
            return Response.success(List.of());
        }

        // 批量查询分类信息
        Set<Long> categoryIds = courses.stream()
                .map(Course::getCategoryId)
                .collect(Collectors.toSet());

        List<CourseCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<CourseCategory>()
                        .in(CourseCategory::getId, categoryIds)
        );
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(CourseCategory::getId, CourseCategory::getName));

        // 批量查询讲师信息
        Set<Long> teacherIds = courses.stream()
                .map(Course::getTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, Teacher> teacherMap;
        if (!teacherIds.isEmpty()) {
            List<Teacher> teachers = teacherMapper.selectList(
                    new LambdaQueryWrapper<Teacher>()
                            .in(Teacher::getId, teacherIds)
            );
            teacherMap = teachers.stream()
                    .collect(Collectors.toMap(Teacher::getId, teacher -> teacher));
        } else {
            teacherMap = Map.of();
        }

        // 转换为VO
        List<WebCourseVO> courseVOList = courses.stream()
                .map(course -> {
                    Teacher teacher = teacherMap.get(course.getTeacherId());
                    String teacherName = teacher != null ? teacher.getName() : "";
                    String teacherAvatar = teacher != null ? teacher.getAvatar() : "";
                    return convertToVO(course, categoryMap, teacherName, teacherAvatar);
                })
                .collect(Collectors.toList());

        log.info("查询分类课程成功，共{}门", courseVOList.size());
        return PageResponse.success(courseVOList, pageNo, coursePage.getTotal(), pageSize);
    }

    @Override
    public Response getCourseDetail(Long courseId) {
        // 注意：课程详情包含用户个性化信息（purchased, isVip），不应该缓存
        // 否则会导致用户状态变更后（如领取VIP），仍然读取到旧的缓存数据
        
        // 查询课程基本信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BizException("课程不存在");
        }

        // 只返回上架状态的课程
        if (course.getStatus() != 1) {
            throw new BizException("课程未上架");
        }

        // 查询分类信息
        CourseCategory category = categoryMapper.selectById(course.getCategoryId());
        String categoryName = category != null ? category.getName() : "";

        // 查询讲师信息
        Teacher teacher = teacherMapper.selectById(course.getTeacherId());
        String teacherName = teacher != null ? teacher.getName() : "";
        String teacherAvatar = teacher != null ? teacher.getAvatar() : "";

        // 实时统计课程的评论总数（包括一级和二级评论）
        Integer totalCommentCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getCourseId, courseId)
                        .eq(Comment::getStatus, 1)
        ).intValue();

        // 查询章节列表
        List<ChapterTreeVO> chapters = buildChapterTree(courseId);

        // 判断用户是否已购买和是否为VIP
        boolean purchased = false;
        boolean isVip = false;

        // 如果用户已登录，检查购买状态和VIP状态
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userMapper.selectById(userId);
            if (user != null) {
                // 判断VIP状态：需要同时满足 isVip=1 且 未过期
                LocalDateTime now = LocalDateTime.now();
                isVip = user.getIsVip() != null && user.getIsVip() == 1 
                        && user.getVipExpireTime() != null 
                        && user.getVipExpireTime().isAfter(now);
                //查询用户是否购买了该课程（需要订单表）
                purchased = checkIfPurchased(userId, courseId);
            }
        }

        // 构建难度名称
        String difficultyName = switch (course.getDifficulty()) {
            case 1 -> "入门";
            case 2 -> "初级";
            case 3 -> "中级";
            case 4 -> "高级";
            default -> "未知";
        };
        LambdaQueryWrapper<CourseChapter> coursrChapterLqw = new LambdaQueryWrapper<>();
        coursrChapterLqw.eq(CourseChapter::getCourseId, courseId);
        coursrChapterLqw.eq(CourseChapter::getStatus, StatusEnum.ABLE.getCode());
        Long chapterCount = courseChapterMapper.selectCount(coursrChapterLqw);
        // 构建VO
        CourseDetailVO courseDetailVO = CourseDetailVO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .subTitle(course.getSubTitle())
                .cover(course.getCover())
                .categoryId(course.getCategoryId())
                .categoryName(categoryName)
                .teacherId(course.getTeacherId())
                .teacherName(teacherName)
                .teacherAvatar(teacherAvatar)
                .description(course.getDescription())
                .outline(course.getOutline())
                .price(course.getPrice())
                .originalPrice(course.getOriginalPrice())
                .isFree(course.getIsFree())
                .difficulty(course.getDifficulty())
                .difficultyName(difficultyName)
                .duration(course.getDuration())
                .viewCount(course.getViewCount())
                .buyCount(course.getBuyCount())
                .likeCount(course.getLikeCount())
                .commentCount(totalCommentCount)  // 使用实时统计的评论总数
                .chapterCount(chapterCount.intValue())
                .totalChapterCount(course.getTotalChapterCount())
                .chapters(chapters)
                .purchased(purchased)
                .isVip(isVip)
                .build();
        
        // 不缓存课程详情，因为包含用户个性化信息
        return Response.success(courseDetailVO);
    }

    private boolean checkIfPurchased(Long userId, Long courseId) {
        int i = courseOrderMapper.findUserIsPuerchased(userId, courseId);
        return i > 0;
    }

    @Override
    public Response getCourseChapters(Long courseId) {
        log.info("查询课程章节，courseId: {}", courseId);
        List<ChapterTreeVO> chapters = buildChapterTree(courseId);
        return Response.success(chapters);
    }

    /**
     * 获取章节列表（扁平结构）
     */
    private List<ChapterTreeVO> buildChapterTree(Long courseId) {
        // 查询该课程的所有章节
        LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseChapter::getCourseId, courseId)
                .eq(CourseChapter::getStatus, 1) // 只查询正常状态
                .orderByAsc(CourseChapter::getCreateTime); // 按创建时间升序排列（从旧到新）

        List<CourseChapter> allChapters = courseChapterMapper.selectList(queryWrapper);

        // 转换为VO（扁平列表）
        return allChapters.stream()
                .map(this::convertToChapterVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换章节为VO
     */
    private ChapterTreeVO convertToChapterVO(CourseChapter chapter) {
        // 格式化视频时长（秒转换为mm:ss格式）
        String durationFormat = "";
        if (chapter.getVideoDuration() != null && chapter.getVideoDuration() > 0) {
            int minutes = chapter.getVideoDuration() / 60;
            int seconds = chapter.getVideoDuration() % 60;
            durationFormat = String.format("%02d:%02d", minutes, seconds);
        }

        return ChapterTreeVO.builder()
                .id(chapter.getId())
                .courseId(chapter.getCourseId())
                .title(chapter.getTitle())
                .videoUrl(chapter.getVideoUrl())
                .videoDuration(chapter.getVideoDuration())
                .videoDurationFormat(durationFormat)
                .parentId(chapter.getParentId())
                .sort(chapter.getSort())
                .isFree(chapter.getIsFree())
                .status(chapter.getStatus())
                .build();
    }

    private WebCourseVO convertToVO(Course course, Map<Long, String> categoryMap, String teacherName, String teacherAvatar) {
        String difficultyName = switch (course.getDifficulty()) {
            case 1 -> "入门";
            case 2 -> "初级";
            case 3 -> "中级";
            case 4 -> "高级";
            default -> "未知";
        };

        // 查询当前实际章节数
        LambdaQueryWrapper<CourseChapter> chapterLqw = new LambdaQueryWrapper<>();
        chapterLqw.eq(CourseChapter::getCourseId, course.getId())
                .eq(CourseChapter::getStatus, 1);
        Long actualChapterCount = courseChapterMapper.selectCount(chapterLqw);

        return WebCourseVO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .subTitle(course.getSubTitle())
                .cover(course.getCover())
                .categoryName(categoryMap.getOrDefault(course.getCategoryId(), ""))
                .teacherName(teacherName)
                .teacherAvatar(teacherAvatar)
                .description(course.getDescription())
                .price(course.getPrice())
                .originalPrice(course.getOriginalPrice())
                .isFree(course.getIsFree())
                .difficulty(course.getDifficulty())
                .difficultyName(difficultyName)
                .duration(course.getDuration())
                .viewCount(course.getViewCount())
                .buyCount(course.getBuyCount())
                .totalChapterCount(course.getTotalChapterCount())
                .chapterCount(actualChapterCount.intValue())
                .build();
    }

    @Override
    public Response getCourseCategories() {
        // 查询所有课程分类（只查询状态为1的）
        LambdaQueryWrapper<CourseCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(CourseCategory::getStatus, 1)
                .orderByAsc(CourseCategory::getSort)
                .orderByAsc(CourseCategory::getId);
        List<CourseCategory> categories = courseCategoryMapper.selectList(categoryWrapper);

        // 查询每个分类下的课程数量（只统计上架状态的课程）
        Map<Long, Long> courseCounts = new HashMap<>();
        for (CourseCategory category : categories) {
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Course::getCategoryId, category.getId())
                    .eq(Course::getStatus, 1); // 只统计上架状态
            Long count = courseMapper.selectCount(queryWrapper);
            courseCounts.put(category.getId(), count);
        }

        // 转换为VO
        List<CourseCategoryVO> categoryVOS = categories.stream()
                .map(category -> {
                    CourseCategoryVO vo = new CourseCategoryVO();
                    vo.setId(category.getId());
                    vo.setName(category.getName());
                    vo.setParentId(category.getParentId());
                    vo.setDescription(null); // 分类表没有description字段，设为null
                    vo.setCourseCount(courseCounts.getOrDefault(category.getId(), 0L).intValue());
                    return vo;
                })
                .collect(Collectors.toList());

        // 构建树形结构
        List<CourseCategoryVO> tree = buildCategoryTree(categoryVOS);

        // 递归计算父分类的课程总数（包含子分类的课程数）
        for (CourseCategoryVO rootCategory : tree) {
            calculateTotalCourseCount(rootCategory);
        }

        return Response.success(tree);
    }
    
    @Override
    public Response searchCourseByTitle(String title) {
        log.info("根据标题搜索课程，关键词: {}", title);
        
        // 模糊查询课程
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Course::getTitle, title)
            .eq(Course::getStatus, 1) // 只查询上架的课程
            .orderByDesc(Course::getBuyCount) // 按购买数量排序
            .last("LIMIT 1"); // 只返回第一个匹配的课程
        
        Course course = courseMapper.selectOne(wrapper);
        
        if (course == null) {
            log.info("未找到匹配的课程");
            return Response.fail("未找到该课程");
        }
        
        // 返回课程基本信息
        Map<String, Object> result = new HashMap<>();
        result.put("id", course.getId());
        result.put("title", course.getTitle());
        
        return Response.success(result);
    }

    /**
     * 构建分类树形结构
     */
    private List<CourseCategoryVO> buildCategoryTree(List<CourseCategoryVO> allCategories) {
        // 找出所有顶级分类（parentId为0或null）
        List<CourseCategoryVO> rootCategories = allCategories.stream()
                .filter(category -> category.getParentId() == null || category.getParentId() == 0)
                .collect(Collectors.toList());

        // 为每个顶级分类递归查找子分类
        for (CourseCategoryVO rootCategory : rootCategories) {
            List<CourseCategoryVO> children = findChildren(rootCategory.getId(), allCategories);
            rootCategory.setChildren(children);
        }

        return rootCategories;
    }

    /**
     * 递归查找子分类
     */
    private List<CourseCategoryVO> findChildren(Long parentId, List<CourseCategoryVO> allCategories) {
        List<CourseCategoryVO> children = allCategories.stream()
                .filter(category -> category.getParentId() != null && category.getParentId().equals(parentId))
                .collect(Collectors.toList());

        // 递归查找每个子分类的子分类
        for (CourseCategoryVO child : children) {
            List<CourseCategoryVO> grandChildren = findChildren(child.getId(), allCategories);
            child.setChildren(grandChildren);
        }

        return children;
    }

    /**
     * 递归计算分类的课程总数（包含所有子分类的课程数）
     *
     * @param category 分类
     * @return 该分类及其所有子分类的课程总数
     */
    private int calculateTotalCourseCount(CourseCategoryVO category) {
        // 如果没有子分类，直接返回当前分类的课程数
        if (category.getChildren() == null || category.getChildren().isEmpty()) {
            return category.getCourseCount();
        }

        // 计算所有子分类的课程数之和
        int childrenCourseCount = 0;
        for (CourseCategoryVO child : category.getChildren()) {
            childrenCourseCount += calculateTotalCourseCount(child);
        }

        // 父分类的课程总数 = 自身的课程数 + 所有子分类的课程数
        int totalCount = category.getCourseCount() + childrenCourseCount;
        category.setCourseCount(totalCount);

        return totalCount;
    }

}

