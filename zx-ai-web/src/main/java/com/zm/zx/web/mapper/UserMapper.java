package com.zm.zx.web.mapper;

import com.zm.zx.web.domain.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24690
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-10-12 19:59:42
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




