package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.CommentCreateDTO;
import com.zm.zx.web.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Web端 - 评论Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/comment")
public class WebCommentController {
    
    private final CommentService commentService;
    
    /**
     * 获取课程的一级评论列表（分页）
     * 每条一级评论包含：第一条回复 + 回复总数
     */
    @ApiOperationLog(description = "获取课程一级评论列表")
    @GetMapping("/list/{courseId}")
    public Response getFirstLevelComments(
            @PathVariable("courseId") Long courseId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return commentService.getFirstLevelComments(courseId, pageNo, pageSize);
    }
    
    /**
     * 获取某评论的回复列表（分页）
     */
    @ApiOperationLog(description = "获取评论回复列表")
    @GetMapping("/replies/{parentId}")
    public Response getRepliesByParentId(
            @PathVariable("parentId") Long parentId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return commentService.getRepliesByParentId(parentId, pageNo, pageSize);
    }
    
    /**
     * 创建评论（一级评论或回复）
     */
    @SaCheckLogin
    @ApiOperationLog(description = "创建评论")
    @PostMapping("/create")
    public Response createComment(@Validated @RequestBody CommentCreateDTO dto) {
        return commentService.createComment(dto);
    }
    
    /**
     * 删除评论（仅本人可删除）
     */
    @SaCheckLogin
    @ApiOperationLog(description = "删除评论")
    @DeleteMapping("/{commentId}")
    public Response deleteComment(@PathVariable("commentId") Long commentId) {
        return commentService.deleteComment(commentId);
    }
    
    /**
     * 点赞评论
     */
    @SaCheckLogin
    @ApiOperationLog(description = "点赞评论")
    @PostMapping("/like/{commentId}")
    public Response likeComment(@PathVariable("commentId") Long commentId) {
        return commentService.likeComment(commentId);
    }
    
    /**
     * 取消点赞评论
     */
    @SaCheckLogin
    @ApiOperationLog(description = "取消点赞评论")
    @DeleteMapping("/like/{commentId}")
    public Response unlikeComment(@PathVariable("commentId") Long commentId) {
        return commentService.unlikeComment(commentId);
    }
}

