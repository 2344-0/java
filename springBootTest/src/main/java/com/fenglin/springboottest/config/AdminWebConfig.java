package com.fenglin.springboottest.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册后台鉴权拦截器。
 * /api/admin/login 不在拦截范围内，否则永远无法登录。
 * 与已有的 CorsConfig 互不冲突（两者都是 WebMvcConfigurer，Spring 会合并）。
 */
@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Resource
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns(
                        "/api/admin/check",
                        "/api/admin/logout",
                        "/api/admin/data",
                        "/api/admin/data/**");
    }
}
