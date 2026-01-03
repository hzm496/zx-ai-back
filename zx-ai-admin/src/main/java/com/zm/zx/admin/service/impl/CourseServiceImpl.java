package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.mapper.CourseCategoryMapper;
import com.zm.zx.common.mapper.CourseChapterMapper;
import com.zm.zx.common.mapper.CourseMapper;
import com.zm.zx.common.mapper.TeacherMapper;
import com.zm.zx.admin.model.dto.CourseAddDTO;
import com.zm.zx.admin.model.dto.CourseUpdateDTO;
import com.zm.zx.admin.model.dto.FindCourseListDTO;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.common.model.course.po.CourseCategory;
import com.zm.zx.common.model.course.po.CourseChapter;
import com.zm.zx.common.model.teacher.po.Teacher;
import com.zm.zx.admin.model.vo.CourseVO;
import com.zm.zx.admin.service.CourseService;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 课程Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    private final CourseCategoryMapper categoryMapper;
    private final TeacherMapper teacherMapper;
    private final CourseChapterMapper courseChapterMapper;
    
    @Override
    public PageResponse findCourseList(FindCourseListDTO findCourseListDTO) {
        Integer pageSize = findCourseListDTO.getPageSize();
        Integer pageNum = findCourseListDTO.getPageNum();
        String title = findCourseListDTO.getTitle();
        Long categoryId = findCourseListDTO.getCategoryId();
        Long teacherId = findCourseListDTO.getTeacherId();
        Integer status = findCourseListDTO.getStatus();
        Integer isFree = findCourseListDTO.getIsFree();
        
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(title), Course::getTitle, title);
        lqw.eq(Objects.nonNull(categoryId), Course::getCategoryId, categoryId);
        lqw.eq(Objects.nonNull(teacherId), Course::getTeacherId, teacherId);
        lqw.eq(Objects.nonNull(status), Course::getStatus, status);
        lqw.eq(Objects.nonNull(isFree), Course::getIsFree, isFree);
        lqw.orderByAsc(Course::getSort);
        lqw.orderByDesc(Course::getCreateTime);
        
        Page<Course> coursePage = this.page(page, lqw);
        List<Course> records = coursePage.getRecords();
        
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }
        
        List<CourseVO> courseVOS = records.stream()
                .map(course -> {
                    CourseVO courseVO = BeanUtil.copyProperties(course, CourseVO.class);
                    // 填充分类名称
                    if (course.getCategoryId() != null) {
                        CourseCategory category = categoryMapper.selectById(course.getCategoryId());
                        if (category != null) {
                            courseVO.setCategoryName(category.getName());
                        }
                    }
                    // 填充讲师名称
                    if (course.getTeacherId() != null) {
                        Teacher teacher = teacherMapper.selectById(course.getTeacherId());
                        if (teacher != null) {
                            courseVO.setTeacherName(teacher.getName());
                        }
                    }
                    return courseVO;
                })
                .collect(Collectors.toList());
        
        return PageResponse.success(courseVOS, pageNum, coursePage.getTotal(), pageSize);
    }
    
    @Override
    public Response addCourse(CourseAddDTO courseAddDTO) {
        // 检查分类是否存在
        CourseCategory category = categoryMapper.selectById(courseAddDTO.getCategoryId());
        if (category == null) {
            throw new BizException("分类不存在");
        }
        
        // 如果指定了讲师，检查讲师是否存在
        if (courseAddDTO.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(courseAddDTO.getTeacherId());
            if (teacher == null) {
                throw new BizException("讲师不存在");
            }
        }
        
        // 添加课程
        Course course = BeanUtil.copyProperties(courseAddDTO, Course.class);
        boolean saved = this.save(course);
        if (!saved) {
            throw new BizException(ResponseEnum.ADD_FAIL);
        }
        
        return Response.success();
    }
    
    @Override
    public Response updateCourse(CourseUpdateDTO courseUpdateDTO) {
        // 检查课程是否存在
        Course course = this.getById(courseUpdateDTO.getId());
        if (course == null) {
            throw new BizException("课程不存在");
        }
        
        // 如果修改了分类，检查新分类是否存在
        if (courseUpdateDTO.getCategoryId() != null) {
            CourseCategory category = categoryMapper.selectById(courseUpdateDTO.getCategoryId());
            if (category == null) {
                throw new BizException("分类不存在");
            }
        }
        
        // 如果修改了讲师，检查新讲师是否存在
        if (courseUpdateDTO.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(courseUpdateDTO.getTeacherId());
            if (teacher == null) {
                throw new BizException("讲师不存在");
            }
        }
        
        // 更新课程信息
        Course updateCourse = BeanUtil.copyProperties(courseUpdateDTO, Course.class);
        boolean updated = this.updateById(updateCourse);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }
        
        return Response.success();
    }
    
    @Override
    public Response deleteCourse(Long id) {
        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }
        return Response.success();
    }
    
    @Override
    public Response getCourseById(Long id) {
        Course course = this.getById(id);
        if (course == null) {
            throw new BizException("课程不存在");
        }
        
        CourseVO courseVO = BeanUtil.copyProperties(course, CourseVO.class);
        
        // 填充分类名称
        if (course.getCategoryId() != null) {
            CourseCategory category = categoryMapper.selectById(course.getCategoryId());
            if (category != null) {
                courseVO.setCategoryName(category.getName());
            }
        }
        
        // 填充讲师名称
        if (course.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                courseVO.setTeacherName(teacher.getName());
            }
        }
        
        // 统计总章节数（只统计正常状态的）
        LambdaQueryWrapper<CourseChapter> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(CourseChapter::getCourseId, id);
        totalWrapper.eq(CourseChapter::getStatus, 1);
        Long totalCount = courseChapterMapper.selectCount(totalWrapper);
        courseVO.setTotalChapterCount(totalCount.intValue());
        
        // 统计试看章节数（is_free = 1 且 status = 1）
        LambdaQueryWrapper<CourseChapter> freeWrapper = new LambdaQueryWrapper<>();
        freeWrapper.eq(CourseChapter::getCourseId, id);
        freeWrapper.eq(CourseChapter::getStatus, 1);
        freeWrapper.eq(CourseChapter::getIsFree, 1);
        Long freeCount = courseChapterMapper.selectCount(freeWrapper);
        courseVO.setTrialChapterCount(freeCount.intValue());
        
        // 判断是否支持试看（有试看章节就算支持）
        courseVO.setIsTrial(freeCount > 0 ? 1 : 0);
        
        log.info("课程ID={}, 总章节数={}, 试看章节数={}", id, totalCount, freeCount);
        
        return Response.success(courseVO);
    }
}

