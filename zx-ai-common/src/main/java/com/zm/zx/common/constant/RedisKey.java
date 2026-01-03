package com.zm.zx.common.constant;

public interface RedisKey {
    //vip信息缓存
    String VIP_PACKAGE_INFO = "vip:package:list";
    //教师列表
    String TEACHER_LIST = "teacher:list";
    //课程详情key
    String COURSE_DETAIL = "course:detail:";
    //活动userid集合
    String ACTIVITY_USER_ID_SET = "activity:user:id:set:";
    //活动库存
    String ACTIVITY_STOCK = "activity:stock:";



    //秒杀key
    String SEC_KILL_KEY = "sec:kill:";


    public static String buildCourseDetailKey(Long courseId) {
        return COURSE_DETAIL + courseId;
    }

    public static String buildSecKillKey(Long userId,Long activityId) {
        return SEC_KILL_KEY + userId+":"+activityId;
    }

    public static String buildActivityUserIdSetKey(Long activityId) {
        return ACTIVITY_USER_ID_SET + activityId;
    }

    public static String buildActivityStockKey(Long activityId) {
        return ACTIVITY_STOCK + activityId;
    }

}
