package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.mapper.TeacherMapper;
import com.zm.zx.admin.model.dto.FindTeacherListDTO;
import com.zm.zx.admin.model.dto.TeacherAddDTO;
import com.zm.zx.admin.model.dto.TeacherUpdateDTO;
import com.zm.zx.common.constant.MQConstants;
import com.zm.zx.common.model.teacher.po.Teacher;
import com.zm.zx.admin.model.vo.TeacherVO;
import com.zm.zx.admin.service.TeacherService;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 讲师Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public PageResponse findTeacherList(FindTeacherListDTO findTeacherListDTO) {
        Integer pageSize = findTeacherListDTO.getPageSize();
        Integer pageNum = findTeacherListDTO.getPageNum();
        String name = findTeacherListDTO.getName();
        String title = findTeacherListDTO.getTitle();
        Integer status = findTeacherListDTO.getStatus();

        Page<Teacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Teacher> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(name), Teacher::getName, name);
        lqw.like(StrUtil.isNotBlank(title), Teacher::getTitle, title);
        lqw.eq(Objects.nonNull(status), Teacher::getStatus, status);
        lqw.orderByAsc(Teacher::getSort);
        lqw.orderByDesc(Teacher::getCreateTime);

        Page<Teacher> teacherPage = this.page(page, lqw);
        List<Teacher> records = teacherPage.getRecords();

        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNum, 0, pageSize);
        }

        List<TeacherVO> teacherVOS = records.stream()
                .map(teacher -> BeanUtil.copyProperties(teacher, TeacherVO.class))
                .collect(Collectors.toList());

        return PageResponse.success(teacherVOS, pageNum, teacherPage.getTotal(), pageSize);
    }

    @Override
    public Response addTeacher(TeacherAddDTO teacherAddDTO) {
        // 检查讲师姓名是否已存在
        LambdaQueryWrapper<Teacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teacher::getName, teacherAddDTO.getName());
        Teacher existTeacher = this.getOne(lqw);
        if (existTeacher != null) {
            throw new BizException("讲师姓名已存在");
        }

        // 添加讲师
        Teacher teacher = BeanUtil.copyProperties(teacherAddDTO, Teacher.class);
        boolean saved = this.save(teacher);
        if (!saved) {
            throw new BizException(ResponseEnum.ADD_FAIL);
        }
        String description = MQConstants.TOPIC_TEACHER_CLEAN;
        rocketMQTemplate.asyncSend(description, " ", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("删除教师缓存消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("删除教师缓存消息发送失败：{}", throwable.getMessage(), throwable);
            }
        });
        return Response.success();
    }

    @Override
    public Response updateTeacher(TeacherUpdateDTO teacherUpdateDTO) {
        // 检查讲师是否存在
        Teacher teacher = this.getById(teacherUpdateDTO.getId());
        if (teacher == null) {
            throw new BizException("讲师不存在");
        }

        // 如果修改了姓名，检查新姓名是否已被其他讲师使用
        if (StrUtil.isNotBlank(teacherUpdateDTO.getName())
                && !teacherUpdateDTO.getName().equals(teacher.getName())) {
            LambdaQueryWrapper<Teacher> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Teacher::getName, teacherUpdateDTO.getName());
            lqw.ne(Teacher::getId, teacherUpdateDTO.getId());
            Teacher existTeacher = this.getOne(lqw);
            if (existTeacher != null) {
                throw new BizException("讲师姓名已存在");
            }
        }

        // 更新讲师信息
        Teacher updateTeacher = BeanUtil.copyProperties(teacherUpdateDTO, Teacher.class);
        boolean updated = this.updateById(updateTeacher);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }
        String description = MQConstants.TOPIC_TEACHER_CLEAN;
        rocketMQTemplate.asyncSend(description, " ", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("删除教师缓存消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("删除教师缓存消息发送失败：{}", throwable.getMessage(), throwable);
            }
        });
        return Response.success();
    }

    @Override
    public Response deleteTeacher(Long id) {
        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }
        String description = MQConstants.TOPIC_TEACHER_CLEAN;
        rocketMQTemplate.asyncSend(description, " ", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("删除教师缓存消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("删除教师缓存消息发送失败：{}", throwable.getMessage(), throwable);
            }
        });
        return Response.success();
    }

    @Override
    public Response getTeacherById(Long id) {
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            throw new BizException("讲师不存在");
        }
        TeacherVO teacherVO = BeanUtil.copyProperties(teacher, TeacherVO.class);
        return Response.success(teacherVO);
    }
}

