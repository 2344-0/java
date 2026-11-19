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
                // 前端页面：http://192.168.137.1:5173（本机局域网 IP）
                // 本机相关主机放行任意端口，覆盖这些场景：
                //   · dev 开发服务器          5173
                //   · vite preview / dist 部署 4173 及自定义端口
                //   · localhost / 127.0.0.1   本机自测
                // 注意：仅适用于本地开发；部署上线后应收紧为具体的生产域名。
                .allowedOriginPatterns(
                        "http://192.168.137.1:[*]",
                        "https://192.168.137.1:[*]",
                        "http://localhost:[*]",
                        "https://localhost:[*]",
                        "http://127.0.0.1:[*]",
                        "https://127.0.0.1:[*]",
                        "http://*:5173",
                        "https://*:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
