package com.fenglin.springboottest.config;

import com.fenglin.springboottest.service.AdminSession;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台接口鉴权：除 /api/admin/login 外，所有管理接口都必须带有效 X-Admin-Token。
 *
 * 未登录 / token 过期时返回 HTTP 200 + JSON { code: 401, msg: ... }，
 * 方便前端统一按 code 判断并跳回后台登录页，而不用额外处理 HTTP 异常。
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Resource
    private AdminSession adminSession;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域预检请求直接放行，否则带自定义头的 POST / PUT / DELETE 会被 CORS 拦下
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(AdminSession.HEADER);
        if (adminSession.isValid(token)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"登录已失效，请重新登录\",\"data\":null}");
        return false;
    }
}
