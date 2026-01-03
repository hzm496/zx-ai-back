package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.web.model.dto.CourseOrderQueryDTO;
import com.zm.zx.web.model.po.CourseOrder;
import com.zm.zx.web.model.vo.CourseOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课程订单 Mapper
 */
@Mapper
public interface CourseOrderMapper extends BaseMapper<CourseOrder> {

    /**
     * 分页查询用户的课程订单列表
     */
    IPage<CourseOrderVO> findUserCourseOrdersPage(Page<CourseOrderVO> page, @Param("userId") Long userId);

    /**
     * 管理员分页查询课程订单列表
     */
    IPage<CourseOrderVO> findCourseOrderListForAdmin(Page<CourseOrderVO> page, @Param("dto") CourseOrderQueryDTO dto);

    int findUserIsPuerchased(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
