package com.fenglin.springboottest.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.AdminLoginDTO;
import com.fenglin.springboottest.entity.Admin;
import com.fenglin.springboottest.mapper.AdminMapper;
import com.fenglin.springboottest.service.AdminSession;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理系统登录接口（只有登录，没有注册）。
 *
 *   POST /api/admin/login   { username, password } -> { token, username, nickname }
 *   GET  /api/admin/check   校验登录态（需 X-Admin-Token）
 *   POST /api/admin/logout  退出登录（需 X-Admin-Token）
 *
 * 管理员数据独立存放在 t_admin，与前台斗者 t_user 互不干扰。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private AdminSession adminSession;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginDTO dto) {
        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        String password = dto.getPassword();

        if (username.isEmpty() || password == null || password.isEmpty()) {
            return Result.fail("请输入管理员账号与执掌秘钥");
        }

        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            return Result.fail("执掌令有误，账号或秘钥不正确");
        }

        String token = adminSession.create(admin);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        return Result.ok("执掌令已验证，欢迎回来", data);
    }

    /**
     * 刷新后台页面时，前端先调此接口确认 token 是否仍然有效
     */
    @GetMapping("/check")
    public Result<?> check(@RequestHeader(value = AdminSession.HEADER, required = false) String token) {
        AdminSession.Session session = adminSession.get(token);
        if (session == null) {
            return Result.fail("登录已失效，请重新登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("username", session.getUsername());
        data.put("nickname", session.getNickname());
        data.put("expireAt", session.getExpireAt());
        return Result.ok("会话有效", data);
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = AdminSession.HEADER, required = false) String token) {
        adminSession.invalidate(token);
        return Result.ok("已退出后台管理");
    }
}
