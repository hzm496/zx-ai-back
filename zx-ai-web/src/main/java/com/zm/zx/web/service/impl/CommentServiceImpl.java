package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.CommentCreateDTO;
import com.zm.zx.web.domain.po.Comment;
import com.zm.zx.web.domain.po.CommentLike;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.web.domain.vo.CommentVO;
import com.zm.zx.web.mapper.CommentLikeMapper;
import com.zm.zx.web.mapper.CommentMapper;
import com.zm.zx.web.mapper.UserMapper;
import com.zm.zx.web.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final UserMapper userMapper;
    
    @Override
    public Response getFirstLevelComments(Long courseId, Integer pageNo, Integer pageSize) {
        log.info("获取课程一级评论列表，courseId: {}, pageNo: {}, pageSize: {}", courseId, pageNo, pageSize);
        
        // 计算偏移量
        long offset = (long) (pageNo - 1) * pageSize;
        
        // 查询一级评论列表
        List<Comment> comments = commentMapper.selectFirstLevelComments(courseId, offset, pageSize);
        if (comments.isEmpty()) {
            log.info("该课程暂无评论");
            return PageResponse.success(List.of(), pageNo, 0L, pageSize);
        }
        
        // 查询总数
        Long totalCount = commentMapper.countFirstLevelComments(courseId);
        
        // 提取所有评论ID
        List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
        
        // 批量查询回复数量
        List<CommentMapper.CommentReplyCount> replyCounts = commentMapper.countRepliesByParentIds(commentIds);
        Map<Long, Integer> replyCountMap = replyCounts.stream()
                .collect(Collectors.toMap(
                        CommentMapper.CommentReplyCount::getParentId,
                        CommentMapper.CommentReplyCount::getReplyCount
                ));
        
        // 批量查询第一条回复
        Map<Long, Comment> firstReplyMap = new HashMap<>();
        for (Long commentId : commentIds) {
            Comment firstReply = commentMapper.selectFirstReplyByParentId(commentId);
            if (firstReply != null) {
                firstReplyMap.put(commentId, firstReply);
            }
        }
        
        // 收集所有需要查询的用户ID（包括评论者和第一条回复的用户）
        Set<Long> userIds = new HashSet<>();
        comments.forEach(c -> userIds.add(c.getUserId()));
        firstReplyMap.values().forEach(r -> {
            userIds.add(r.getUserId());
            if (r.getReplyToUserId() != null) {
                userIds.add(r.getReplyToUserId());
            }
        });
        
        // 批量查询用户信息
        Map<Long, User> userMap = batchQueryUsers(userIds);
        
        // 查询当前用户的点赞状态（如果已登录）
        final Set<Long> likedCommentIds;
        if (StpUtil.isLogin()) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            List<Long> allCommentIds = new ArrayList<>(commentIds);
            allCommentIds.addAll(firstReplyMap.values().stream().map(Comment::getId).collect(Collectors.toList()));
            likedCommentIds = queryUserLikedComments(currentUserId, allCommentIds);
        } else {
            likedCommentIds = new HashSet<>();
        }
        
        // 转换为VO
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVO vo = convertToVO(comment, userMap);
            
            // 设置点赞状态
            vo.setIsLiked(likedCommentIds.contains(comment.getId()));
            
            // 设置回复数量
            vo.setReplyCount(replyCountMap.getOrDefault(comment.getId(), 0));
            
            // 设置第一条回复
            Comment firstReply = firstReplyMap.get(comment.getId());
            if (firstReply != null) {
                CommentVO firstReplyVO = convertToVO(firstReply, userMap);
                firstReplyVO.setIsLiked(likedCommentIds.contains(firstReply.getId()));
                vo.setFirstReply(firstReplyVO);
            }
            
            commentVOList.add(vo);
        }
        
        log.info("查询到{}条一级评论", commentVOList.size());
        return PageResponse.success(commentVOList, pageNo, totalCount, pageSize);
    }
    
    @Override
    public Response getRepliesByParentId(Long parentId, Integer pageNo, Integer pageSize) {
        log.info("获取评论回复列表，parentId: {}, pageNo: {}, pageSize: {}", parentId, pageNo, pageSize);
        
        // 验证父评论是否存在
        Comment parentComment = commentMapper.selectById(parentId);
        if (parentComment == null || parentComment.getStatus() != 1) {
            throw new BizException("父评论不存在");
        }
        
        // 计算偏移量
        long offset = (long) (pageNo - 1) * pageSize;
        
        // 查询回复列表
        List<Comment> replies = commentMapper.selectRepliesByParentId(parentId, offset, pageSize);
        if (replies.isEmpty()) {
            log.info("该评论暂无回复");
            return PageResponse.success(List.of(), pageNo, 0L, pageSize);
        }
        
        // 查询总数
        Long totalCount = commentMapper.countRepliesByParentId(parentId);
        
        // 收集所有用户ID
        Set<Long> userIds = new HashSet<>();
        replies.forEach(r -> {
            userIds.add(r.getUserId());
            if (r.getReplyToUserId() != null) {
                userIds.add(r.getReplyToUserId());
            }
        });
        
        // 批量查询用户信息
        Map<Long, User> userMap = batchQueryUsers(userIds);
        
        // 查询当前用户的点赞状态（如果已登录）
        final Set<Long> likedCommentIds;
        if (StpUtil.isLogin()) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            List<Long> commentIds = replies.stream().map(Comment::getId).collect(Collectors.toList());
            likedCommentIds = queryUserLikedComments(currentUserId, commentIds);
        } else {
            likedCommentIds = new HashSet<>();
        }
        
        // 转换为VO
        List<CommentVO> replyVOList = replies.stream()
                .map(reply -> {
                    CommentVO vo = convertToVO(reply, userMap);
                    vo.setIsLiked(likedCommentIds.contains(reply.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
        
        log.info("查询到{}条回复", replyVOList.size());
        return PageResponse.success(replyVOList, pageNo, totalCount, pageSize);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createComment(CommentCreateDTO dto) {
        log.info("创建评论，dto: {}", dto);
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证评论内容长度
        if (dto.getContent().length() > 1000) {
            throw new BizException("评论内容不能超过1000个字符");
        }
        
        // 如果是回复评论，验证父评论是否存在
        Long parentId = dto.getParentId() != null ? dto.getParentId() : 0L;
        if (parentId > 0) {
            Comment parentComment = commentMapper.selectById(parentId);
            if (parentComment == null || parentComment.getStatus() != 1) {
                throw new BizException("父评论不存在");
            }
            // 只支持两级评论，不允许回复的回复
            if (parentComment.getParentId() != 0) {
                throw new BizException("不支持三级评论");
            }
        }
        
        // 构建评论对象
        Comment comment = Comment.builder()
                .userId(userId)
                .courseId(dto.getCourseId())
                .content(dto.getContent())
                .rating(dto.getRating())
                .parentId(parentId)
                .replyToUserId(dto.getReplyToUserId())
                .likeCount(0)
                .status(1) // 默认已通过（如需审核，可改为0）
                .build();
        
        // 插入数据库
        int result = commentMapper.insert(comment);
        if (result > 0) {
            log.info("评论创建成功，commentId: {}", comment.getId());
            return Response.success("评论成功");
        } else {
            throw new BizException("评论失败");
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteComment(Long commentId) {
        log.info("删除评论，commentId: {}", commentId);
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException("评论不存在");
        }
        
        // 验证权限（只能删除自己的评论）
        if (!comment.getUserId().equals(userId)) {
            throw new BizException("无权删除该评论");
        }
        
        // 软删除（修改状态为2）
        comment.setStatus(2);
        int result = commentMapper.updateById(comment);
        
        if (result > 0) {
            log.info("评论删除成功");
            return Response.success("删除成功");
        } else {
            throw new BizException("删除失败");
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response likeComment(Long commentId) {
        log.info("点赞评论，commentId: {}", commentId);
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getStatus() != 1) {
            throw new BizException("评论不存在");
        }
        
        // 检查是否已点赞
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getUserId, userId)
                .eq(CommentLike::getCommentId, commentId);
        Long count = commentLikeMapper.selectCount(queryWrapper);
        
        if (count > 0) {
            throw new BizException("已经点赞过了");
        }
        
        // 插入点赞记录
        CommentLike commentLike = CommentLike.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
        commentLikeMapper.insert(commentLike);
        
        // 更新评论点赞数
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentMapper.updateById(comment);
        
        log.info("点赞成功");
        return Response.success("点赞成功");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response unlikeComment(Long commentId) {
        log.info("取消点赞评论，commentId: {}", commentId);
        
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException("评论不存在");
        }
        
        // 查询点赞记录
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getUserId, userId)
                .eq(CommentLike::getCommentId, commentId);
        CommentLike commentLike = commentLikeMapper.selectOne(queryWrapper);
        
        if (commentLike == null) {
            throw new BizException("未点赞过");
        }
        
        // 删除点赞记录
        commentLikeMapper.deleteById(commentLike.getId());
        
        // 更新评论点赞数
        if (comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentMapper.updateById(comment);
        }
        
        log.info("取消点赞成功");
        return Response.success("取消点赞成功");
    }
    
    /**
     * 批量查询用户信息
     */
    private Map<Long, User> batchQueryUsers(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        List<User> users = userMapper.selectList(queryWrapper);
        
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }
    
    /**
     * 查询用户点赞的评论ID集合
     */
    private Set<Long> queryUserLikedComments(Long userId, List<Long> commentIds) {
        if (commentIds.isEmpty()) {
            return Set.of();
        }
        
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getUserId, userId)
                .in(CommentLike::getCommentId, commentIds);
        List<CommentLike> likes = commentLikeMapper.selectList(queryWrapper);
        
        return likes.stream()
                .map(CommentLike::getCommentId)
                .collect(Collectors.toSet());
    }
    
    /**
     * 将Comment PO 转换为 CommentVO
     */
    private CommentVO convertToVO(Comment comment, Map<Long, User> userMap) {
        User user = userMap.get(comment.getUserId());
        User replyToUser = comment.getReplyToUserId() != null ? userMap.get(comment.getReplyToUserId()) : null;
        
        return CommentVO.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .username(user != null ? user.getUsername() : "")
                .nickname(user != null ? user.getNickname() : "匿名用户")
                .avatar(user != null ? user.getAvatar() : "")
                .courseId(comment.getCourseId())
                .content(comment.getContent())
                .rating(comment.getRating())
                .parentId(comment.getParentId())
                .replyToUserId(comment.getReplyToUserId())
                .replyToNickname(replyToUser != null ? replyToUser.getNickname() : null)
                .likeCount(comment.getLikeCount())
                .isLiked(false) // 默认未点赞，调用方需要单独设置
                .createTime(comment.getCreateTime())
                .build();
    }
}

