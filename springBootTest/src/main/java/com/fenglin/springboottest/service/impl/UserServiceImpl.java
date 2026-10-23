package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginDTO;
import com.fenglin.springboottest.dto.RegisterDTO;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.UserMapper;
import com.fenglin.springboottest.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 注册 / 登录业务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * BCrypt 加密器（线程安全，可直接作为成员变量复用）
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result<?> register(RegisterDTO dto) {
        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        String password = dto.getPassword();
        String phone = dto.getPhone() == null ? null : dto.getPhone().trim();

        // 1. 参数校验（前端已校验过，后端再做一次兜底）
        if (username.length() < 3) {
            return Result.fail("账号至少 3 个字符");
        }
        if (password == null || password.length() < 6) {
            return Result.fail("密码不得少于 6 位");
        }

        // 2. 账号重复校验
        User existUser = baseMapper.selectByUsername(username);
        if (existUser != null) {
            return Result.fail("该斗者账号已被注册");
        }

        // 3. 手机号重复校验
        if (phone != null && !phone.isEmpty()) {
            User existPhone = baseMapper.selectByPhone(phone);
            if (existPhone != null) {
                return Result.fail("该手机号已被绑定");
            }
        }

        // 4. 落库：密码以 BCrypt 密文保存
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);

        boolean saved = this.save(user);
        if (!saved) {
            return Result.fail("注册失败，请稍后再试");
        }
        return Result.ok("契约已成，异火待燃，欢迎加入斗气大陆！");
    }

    @Override
    public Result<?> login(LoginDTO dto) {
        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        String password = dto.getPassword();

        if (username.isEmpty() || password == null || password.isEmpty()) {
            return Result.fail("请输入斗者账号与秘钥");
        }

        User user = baseMapper.selectByUsername(username);
        if (user == null) {
            return Result.fail("斗者不存在，请先立下契约");
        }

        // 明文密码与数据库密文比对
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.fail("斗气秘钥错误");
        }

        // 登录成功，返回用户名与一个简单 token（演示用，生产请改用 JWT）
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("token", UUID.randomUUID().toString().replace("-", ""));

        return Result.ok("登录成功，异火认主，斗气涌入经脉……", data);
    }
}
