package com.zm.zx.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zm.zx.common.mapper.CourseChapterMapper;
import com.zm.zx.admin.model.dto.ChapterAddDTO;
import com.zm.zx.admin.model.dto.ChapterUpdateDTO;
import com.zm.zx.common.model.course.po.CourseChapter;
import com.zm.zx.admin.model.vo.CourseChapterVO;
import com.zm.zx.admin.service.CourseChapterService;
import com.zm.zx.common.enums.ResponseEnum;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程章节Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {

    @Override
    public Response getChapterTree(Long courseId) {
        log.info("查询课程ID={}的章节列表", courseId);

        // 查询该课程的所有章节
        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getCourseId, courseId);
        wrapper.orderByAsc(CourseChapter::getCreateTime);

        List<CourseChapter> allChapters = this.list(wrapper);
        log.info("查询到{}条章节数据", allChapters.size());

        // 转换为VO
        List<CourseChapterVO> chapterVOList = allChapters.stream()
                .map(chapter -> BeanUtil.copyProperties(chapter, CourseChapterVO.class))
                .collect(Collectors.toList());

        // 构建树形结构
        List<CourseChapterVO> tree = buildTree(chapterVOList);

        return Response.success(tree);
    }

    /**
     * 构建树形结构
     */
    private List<CourseChapterVO> buildTree(List<CourseChapterVO> allChapters) {
        List<CourseChapterVO> tree = new ArrayList<>();

        // 找出所有章（parentId为0或null）
        for (CourseChapterVO chapter : allChapters) {
            if (chapter.getParentId() == null || chapter.getParentId() == 0) {
                chapter.setChildren(new ArrayList<>());
                tree.add(chapter);
            }
        }

        // 为每个章添加其子节（节）
        for (CourseChapterVO parent : tree) {
            for (CourseChapterVO chapter : allChapters) {
                if (chapter.getParentId() != null && chapter.getParentId().equals(parent.getId())) {
                    parent.getChildren().add(chapter);
                }
            }
        }

        // 【修复】如果没有顶级节点，把所有找不到父节点的节点也作为顶级节点展示
        if (tree.isEmpty()) {
            log.warn("未找到parent_id=0的顶级章节，将所有孤立节点作为顶级节点展示");
            // 把所有节点都作为顶级节点（不理想，但至少能显示数据）
            for (CourseChapterVO chapter : allChapters) {
                chapter.setChildren(new ArrayList<>());
            }
            return allChapters;
        }

        return tree;
    }

    @Override
    public Response addChapter(ChapterAddDTO chapterAddDTO) {
        CourseChapter chapter = BeanUtil.copyProperties(chapterAddDTO, CourseChapter.class);

        // 如果parentId为null，设置为0
        if (chapter.getParentId() == null) {
            chapter.setParentId(0L);
        }

        // 如果sort为null，设置为0
        if (chapter.getSort() == null) {
            chapter.setSort(0);
        }

        // 如果isFree为null，默认为0（不免费）
        if (chapter.getIsFree() == null) {
            chapter.setIsFree(0);
        }

        // 如果status为null，默认为1（正常）
        if (chapter.getStatus() == null) {
            chapter.setStatus(1);
        }

        boolean saved = this.save(chapter);
        if (!saved) {
            throw new BizException(ResponseEnum.ADD_FAIL);
        }

        return Response.success();
    }

    @Override
    public Response updateChapter(ChapterUpdateDTO chapterUpdateDTO) {
        // 检查章节是否存在
        CourseChapter chapter = this.getById(chapterUpdateDTO.getId());
        if (chapter == null) {
            throw new BizException("章节不存在");
        }

        // 更新章节
        CourseChapter updateChapter = BeanUtil.copyProperties(chapterUpdateDTO, CourseChapter.class);
        boolean updated = this.updateById(updateChapter);
        if (!updated) {
            throw new BizException(ResponseEnum.UPDATE_FAIL);
        }

        return Response.success();
    }

    @Override
    public Response deleteChapter(Long id) {
        // 检查是否有子章节
        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getParentId, id);
        long count = this.count(wrapper);

        if (count > 0) {
            throw new BizException("该章节下还有子章节，无法删除");
        }

        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BizException(ResponseEnum.DELETE_FAIL);
        }

        return Response.success();
    }

    @Override
    public Response getChapterById(Long id) {
        CourseChapter chapter = this.getById(id);
        if (chapter == null) {
            throw new BizException("章节不存在");
        }

        CourseChapterVO chapterVO = BeanUtil.copyProperties(chapter, CourseChapterVO.class);
        return Response.success(chapterVO);
    }
}

