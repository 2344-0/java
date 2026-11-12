package com.fenglin.springboottest.config;

import com.fenglin.springboottest.entity.Admin;
import com.fenglin.springboottest.mapper.AdminMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 首次启动时自动写入默认管理员账号，省去手工计算 BCrypt 密文的麻烦。
 *
 * 前置条件：已执行 sql/admin.sql 建表。
 * 默认账号：admin / admin123（可在后台数据库中自行修改）
 * 只有当 t_admin 一张记录都没有时才会写入，不会覆盖已有管理员。
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Resource
    private AdminMapper adminMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) {
        try {
            Long count = adminMapper.selectCount(null);
            if (count != null && count > 0) {
                return;
            }
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("大陆掌事");
            adminMapper.insert(admin);
            log.info("[后台管理] 已初始化默认管理员：admin / admin123（请尽快修改密码）");
        } catch (Exception e) {
            log.warn("[后台管理] 初始化默认管理员失败，请先执行 sql/admin.sql 建表：{}", e.getMessage());
        }
    }
}
