package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.mapper.UserCourseMapper;
import com.zm.zx.web.model.vo.MyCourseVO;
import com.zm.zx.web.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户课程服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCourseServiceImpl implements UserCourseService {

    private final UserCourseMapper userCourseMapper;

    @Override
    public PageResponse getUserCoursesPage(Integer pageNo, Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("分页查询用户 {} 的课程列表，页码：{}，每页数量：{}", userId, pageNo, pageSize);

        Page<MyCourseVO> page = new Page<>(pageNo, pageSize);
        IPage<MyCourseVO> resultPage = userCourseMapper.findUserCoursesPage(page, userId);

        List<MyCourseVO> records = resultPage.getRecords();
        if (records.isEmpty()) {
            return PageResponse.success(List.of(), pageNo, 0, pageSize);
        }

        return PageResponse.success(records, pageNo, resultPage.getTotal(), pageSize);
    }

    @Override
    public Response checkUserHasCourse(Long courseId) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("检查用户 {} 是否拥有课程 {}", userId, courseId);

        boolean hasCourse = userCourseMapper.checkUserHasCourse(userId, courseId);

        Map<String, Object> result = new HashMap<>();
        result.put("hasCourse", hasCourse);

        return Response.success(result);
    }
}

