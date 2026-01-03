package com.zm.zx.auth;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer, Ordered {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册 Sa-Token 拦截器");
        // 注册 Sa-Token 拦截器，定义详细认证规则 
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 指定一条 match 规则
            SaRouter
                    .match("/**")    // 拦截的 path 列表，可以写多个 */
                    // 系统错误页面 - 无需登录（避免上下文初始化异常）
                    .notMatch("/error")
                    // 用户认证相关接口 - 无需登录
                    .notMatch("/web/user/login")
                    .notMatch("/web/user/register")
                    // 学习路径相关接口 - 无需登录（浏览学习路径）
                    .notMatch("/web/learning-path/list")
                    .notMatch("/web/learning-path/list/category/**")
                    .notMatch("/web/learning-path/detail/**")
                    // 课程分类相关接口 - 无需登录（浏览分类）
                    .notMatch("/web/category/**")
                    // 讲师相关接口 - 无需登录（浏览讲师）
                    .notMatch("/web/teacher/**")
                    // 课程相关接口 - 无需登录（浏览课程）
                    .notMatch("/web/course/**")
                    // 评论相关接口 - 无需登录（查看评论和回复）
                    .notMatch("/web/comment/list/**")
                    .notMatch("/web/comment/replies/**")
                    // 活动相关接口 - 无需登录（查看活动列表）
                    .notMatch("/web/activity/list")
                    // VIP相关接口 - 无需登录（查看套餐）
                    .notMatch("/web/vip/packages")
                    // 文件访问接口 - 无需登录（可能需要查看公开资源）
                    .notMatch("/web/file/download/**")
                    // 支付宝支付相关接口 - 无需登录（支付宝回调接口）
                    .notMatch("/web/alipay/**")
                    .notMatch("/web/system-config/**")
                    .check(r -> StpUtil.checkLogin());        // 要执行的校验动作，可以写完整的 lambda 表达式

            // 根据路由划分模块，不同模块不同鉴权
//            SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//            SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//            SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//            SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
//            SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
//            SaRouter.match("/comment/**", r -> StpUtil.checkPermission("comment"));
        }))
        .addPathPatterns("/**")
        .excludePathPatterns("/error"); // 排除错误页面，避免上下文初始化异常
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
