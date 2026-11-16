package com.fenglin.springboottest.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 首次启动时自动写入默认管理员账号。
 *
 * 管理员身份统一存放在 t_user：permission = 0 即为管理员，密码按需求明文保存。
 * 默认账号：admin / admin123（请在后台数据库中自行修改）
 * 只有当 t_user 中不存在任何 permission = 0 的账号时才会写入，不会覆盖已有管理员。
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    /** 管理员权限值 */
    private static final int PERMISSION_ADMIN = 0;

    @Resource
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>().eq(User::getPermission, PERMISSION_ADMIN));
            if (count != null && count > 0) {
                return;
            }

            // 账号名若已被普通斗者占用，则不再创建，避免把普通账号提权
            if (userMapper.selectByUsername("admin") != null) {
                log.warn("[后台管理] 账号 admin 已存在且非管理员，跳过默认管理员初始化");
                return;
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setPermission(PERMISSION_ADMIN);
            userMapper.insert(admin);
            log.info("[后台管理] 已初始化默认管理员：admin / admin123（permission=0，请尽快修改密码）");
        } catch (Exception e) {
            log.warn("[后台管理] 初始化默认管理员失败，请确认 t_user 表已包含 permission 字段：{}", e.getMessage());
        }
    }
}
