package com.zm.zx.common.utils;

import cn.hutool.core.util.RandomUtil;

public class RedisUtils {


    //过期时间+随机秒数
    public static Long getRandomExpireTime() {
        return 60 * 60 * 24 + RandomUtil.randomLong(60);
    }
}
