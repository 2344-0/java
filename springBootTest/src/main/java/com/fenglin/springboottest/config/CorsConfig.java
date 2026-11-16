package com.fenglin.springboottest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置。
 * Vue 开发服务器跑在 5173，后端跑在 8081，端口不同即跨域，必须放行前端才能访问接口。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 前端地址不再写死具体 IP：只要来源端口是 5173（Vite 开发服务器）就放行，
                // 这样 localhost / 127.0.0.1 / 任意局域网 IP 访问都能正常联调。
                // 注意：仅适用于本地开发；部署上线后应收紧为具体的生产域名。
                .allowedOriginPatterns(
                        "http://*:5173",
                        "https://*:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
