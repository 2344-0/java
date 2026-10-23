package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginDTO;
import com.fenglin.springboottest.dto.RegisterDTO;
import com.fenglin.springboottest.entity.User;

public interface UserService extends IService<User> {

    /**
     * 注册斗者
     */
    Result<?> register(RegisterDTO dto);

    /**
     * 账号 + 密码登录
     */
    Result<?> login(LoginDTO dto);
}
