package com.zm.zx.web.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.mapper.CourseMapper;
import com.zm.zx.common.model.teacher.po.Teacher;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.common.utils.RedisUtils;
import com.zm.zx.common.model.course.po.Course;
import com.zm.zx.web.domain.vo.WebTeacherVO;
import com.zm.zx.web.enums.StatusEnum;
import com.zm.zx.web.mapper.WebTeacherMapper;
import com.zm.zx.web.service.WebTeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zm.zx.common.constant.RedisKey.TEACHER_LIST;

/**
 * Web端 - 讲师Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebTeacherServiceImpl implements WebTeacherService {

    private final WebTeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response findAllTeachers() {
        // 查询所有正常状态的讲师，按排序字段升序排列
        String teacherListInfoJson = (String) redisTemplate.opsForValue().get(TEACHER_LIST);
        if (Objects.nonNull(teacherListInfoJson)) {
            List<WebTeacherVO> teacherVOList = JSONUtil.toList(teacherListInfoJson, WebTeacherVO.class);
            return Response.success(teacherVOList);
        }
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getStatus, StatusEnum.ABLE.getCode()) // 只查询正常状态
                .orderByAsc(Teacher::getSort)   // 按排序升序
                .orderByDesc(Teacher::getCreateTime); // 相同排序值时按创建时间倒序

        List<Teacher> teachers = teacherMapper.selectList(queryWrapper);

        // 转换为VO
        List<WebTeacherVO> teacherVOList = teachers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        //保底一天+——随机秒数
        redisTemplate.opsForValue().set(TEACHER_LIST, JSONUtil.toJsonStr(teacherVOList), RedisUtils.getRandomExpireTime(), TimeUnit.SECONDS);
        return Response.success(teacherVOList);
    }

    @Override
    public Response getTeacherListByPage(Integer pageNo, Integer pageSize) {
        log.info("分页查询讲师列表，pageNo: {}, pageSize: {}", pageNo, pageSize);

        // 创建分页对象
        Page<Teacher> page = new Page<>(pageNo, pageSize);

        // 查询所有正常状态的讲师，按排序字段升序排列
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getStatus, StatusEnum.ABLE.getCode()) // 只查询正常状态
                .orderByAsc(Teacher::getSort)   // 按排序升序
                .orderByDesc(Teacher::getCreateTime); // 相同排序值时按创建时间倒序

        Page<Teacher> teacherPage = teacherMapper.selectPage(page, queryWrapper);

        // 转换为VO
        List<WebTeacherVO> teacherVOList = teacherPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询到讲师{}位，总记录数：{}", teacherVOList.size(), teacherPage.getTotal());
        return PageResponse.success(teacherVOList, pageNo, teacherPage.getTotal(), pageSize);
    }

    /**
     * 将Teacher PO 转换为 VO
     */
    private WebTeacherVO convertToVO(Teacher teacher) {
        // 统计该讲师的课程数量（只统计上架状态的课程）
        LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();
        courseQueryWrapper.eq(Course::getTeacherId, teacher.getId())
                .eq(Course::getStatus, StatusEnum.ABLE.getCode()); // 只统计上架状态的课程
        return WebTeacherVO.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .avatar(teacher.getAvatar())
                .title(teacher.getTitle())
                .intro(teacher.getIntro())
                .experience(teacher.getExperience())
                .build();
    }
}

