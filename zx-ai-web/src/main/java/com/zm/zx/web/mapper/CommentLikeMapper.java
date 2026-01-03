package com.zm.zx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.zx.web.domain.po.CommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论点赞 Mapper
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
}

