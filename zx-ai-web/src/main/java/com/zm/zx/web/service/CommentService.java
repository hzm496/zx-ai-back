package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.CommentCreateDTO;

/**
 * 评论 Service
 */
public interface CommentService {
    
    /**
     * 获取课程的一级评论列表（分页）
     * 每条一级评论包含：第一条回复 + 回复总数
     * 
     * @param courseId 课程ID
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    Response getFirstLevelComments(Long courseId, Integer pageNo, Integer pageSize);
    
    /**
     * 获取某评论的回复列表（分页）
     * 
     * @param parentId 父评论ID
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 回复列表
     */
    Response getRepliesByParentId(Long parentId, Integer pageNo, Integer pageSize);
    
    /**
     * 创建评论（一级评论或回复）
     * 
     * @param dto 评论创建DTO
     * @return 创建结果
     */
    Response createComment(CommentCreateDTO dto);
    
    /**
     * 删除评论（仅本人可删除）
     * 
     * @param commentId 评论ID
     * @return 删除结果
     */
    Response deleteComment(Long commentId);
    
    /**
     * 点赞评论
     * 
     * @param commentId 评论ID
     * @return 点赞结果
     */
    Response likeComment(Long commentId);
    
    /**
     * 取消点赞评论
     * 
     * @param commentId 评论ID
     * @return 取消点赞结果
     */
    Response unlikeComment(Long commentId);
}

