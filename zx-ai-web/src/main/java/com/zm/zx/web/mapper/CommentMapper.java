package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.domain.po.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 查询课程的一级评论列表（分页）
     * @param courseId 课程ID
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 评论列表
     */
    List<Comment> selectFirstLevelComments(@Param("courseId") Long courseId,
                                           @Param("offset") Long offset,
                                           @Param("pageSize") Integer pageSize);
    
    /**
     * 统计课程的一级评论总数
     * @param courseId 课程ID
     * @return 总数
     */
    Long countFirstLevelComments(@Param("courseId") Long courseId);
    
    /**
     * 查询某评论的回复列表（分页）
     * @param parentId 父评论ID
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 回复列表
     */
    List<Comment> selectRepliesByParentId(@Param("parentId") Long parentId,
                                          @Param("offset") Long offset,
                                          @Param("pageSize") Integer pageSize);
    
    /**
     * 统计某评论的回复总数
     * @param parentId 父评论ID
     * @return 总数
     */
    Long countRepliesByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询某评论的第一条回复
     * @param parentId 父评论ID
     * @return 第一条回复
     */
    Comment selectFirstReplyByParentId(@Param("parentId") Long parentId);
    
    /**
     * 批量查询评论的回复数量
     * @param commentIds 评论ID列表
     * @return 评论ID和回复数量的映射
     */
    List<CommentReplyCount> countRepliesByParentIds(@Param("commentIds") List<Long> commentIds);
    
    /**
     * 评论回复数量结果类
     */
    class CommentReplyCount {
        private Long parentId;
        private Integer replyCount;
        
        public Long getParentId() {
            return parentId;
        }
        
        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }
        
        public Integer getReplyCount() {
            return replyCount;
        }
        
        public void setReplyCount(Integer replyCount) {
            this.replyCount = replyCount;
        }
    }
}

