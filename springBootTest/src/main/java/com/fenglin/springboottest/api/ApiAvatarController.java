package com.fenglin.springboottest.api;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.UserAvatar;
import com.fenglin.springboottest.mapper.UserAvatarMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 塑颜阁形象接口：供 Vue 前端「塑颜阁」页面保存/读取捏脸数据。
 *
 * 路径约定：
 *   GET  /api/avatar?username=xxx          读取形象配置（无记录返回 code=0, data=null）
 *   POST /api/avatar  { username, config } 保存形象配置（存在则更新，不存在则插入）
 * 响应统一为 { code, msg, data }，code = 0 表示成功。
 *
 * 说明：前台登录态仅携带 username（无 token），与项目现有登录体系一致，
 * 因此以 username 作为身份标识。config 为前端形象对象的 JSON 字符串。
 */
@RestController
@RequestMapping("/api/avatar")
public class ApiAvatarController {

    @Resource
    private UserAvatarMapper userAvatarMapper;

    /**
     * 读取当前斗者的形象配置
     */
    @GetMapping
    public Result<Map<String, Object>> get(@RequestParam(required = false) String username) {
        Map<String, Object> data = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            data.put("config", null);
            return Result.ok("未指定斗者", data);
        }
        UserAvatar record = userAvatarMapper.selectByUsername(username.trim());
        data.put("config", record == null ? null : record.getConfig());
        return Result.ok("已获取形象配置", data);
    }

    /**
     * 保存当前斗者的形象配置（upsert）
     */
    @PostMapping
    public Result<?> save(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String config = body.get("config");
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("请先登录再保存形象");
        }
        if (config == null || config.isEmpty()) {
            return Result.fail("形象配置为空");
        }
        username = username.trim();

        UserAvatar existing = userAvatarMapper.selectByUsername(username);
        if (existing == null) {
            UserAvatar row = new UserAvatar();
            row.setUsername(username);
            row.setConfig(config);
            userAvatarMapper.insert(row);
        } else {
            UserAvatar update = new UserAvatar();
            update.setConfig(config);
            userAvatarMapper.update(update,
                    new LambdaUpdateWrapper<UserAvatar>().eq(UserAvatar::getUsername, username));
        }
        return Result.ok("形貌已镌刻");
    }
}
