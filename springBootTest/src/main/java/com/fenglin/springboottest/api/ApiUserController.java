package com.fenglin.springboottest.api;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginDTO;
import com.fenglin.springboottest.dto.RegisterDTO;
import com.fenglin.springboottest.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供给 Vue 前端的 REST 接口。
 *
 * 路径与前端约定保持一致（RegisterPanel.vue / LoginPanel.vue）：
 *   POST /api/user/register   请求体 { user, pwd, phone }
 *   POST /api/user/login      请求体 { user, pwd }
 * 响应统一为 { code, msg, data }，code = 0 表示成功。
 *
 * 注意：本类映射 /api/user，与原有的 @Controller UserController（映射 /user）互不冲突。
 */
@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }
}
