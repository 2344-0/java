package com.fenglin.springboottest.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.Sect;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.SectMapper;
import com.fenglin.springboottest.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 宗门加入接口：供 Vue 前端「宗门」页面使用。
 *
 * 路径约定：
 *   GET  /api/sect/mine?username=xxx   查询当前斗者所属宗门（含宗门详情）
 *   GET  /api/sect/members             统计各宗门成员数（宗门名 -> 人数）
 *   POST /api/sect/join     { username, sect }  加入宗门（宗门须存在于势力榜）
 *   POST /api/sect/leave    { username }        退出宗门
 * 响应统一为 { code, msg, data }，code = 0 表示成功。
 *
 * 说明：前台登录态仅携带 username（无 token），因此接口以 username 作为身份标识，
 * 与项目现有登录体系保持一致；手机验证码登录等未入库的用户在首次加入时补建占位记录。
 */
@RestController
@RequestMapping("/api/sect")
public class ApiSectController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SectMapper sectMapper;

    /**
     * 查询当前斗者所属宗门
     */
    @GetMapping("/mine")
    public Result<Map<String, Object>> mine(@RequestParam(required = false) String username) {
        Map<String, Object> data = new HashMap<>();
        String sect = null;
        if (username != null && !username.trim().isEmpty()) {
            User user = userMapper.selectByUsername(username.trim());
            if (user != null) sect = user.getSect();
        }
        data.put("sect", sect == null ? "" : sect);

        Sect info = null;
        if (sect != null && !sect.isEmpty()) {
            info = sectMapper.selectOne(new LambdaQueryWrapper<Sect>().eq(Sect::getName, sect));
        }
        data.put("sectInfo", info);
        return Result.ok("已获取所属宗门", data);
    }

    /**
     * 统计各宗门成员数
     */
    @GetMapping("/members")
    public Result<Map<String, Integer>> members() {
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> row : userMapper.countBySect()) {
            Object s = row.get("sect");
            Object c = row.get("cnt");
            if (s != null) {
                map.put(s.toString(), c == null ? 0 : ((Number) c).intValue());
            }
        }
        return Result.ok("宗门人数已就绪", map);
    }

    /**
     * 加入宗门
     */
    @PostMapping("/join")
    public Result<?> join(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String sect = body.get("sect");
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("请先登录再加入宗门");
        }
        if (sect == null || sect.trim().isEmpty()) {
            return Result.fail("请选择要加入的宗门");
        }
        username = username.trim();
        sect = sect.trim();

        Sect target = sectMapper.selectOne(new LambdaQueryWrapper<Sect>().eq(Sect::getName, sect));
        if (target == null) {
            return Result.fail("该宗门不在势力榜之中");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            // 兼容手机验证码登录等未入库用户：补建一条占位记录
            user = new User();
            user.setUsername(username);
            user.setPassword("");
            user.setPermission(1);
            user.setSect(sect);
            userMapper.insert(user);
        } else {
            if (sect.equals(user.getSect())) {
                return Result.fail("你已归属于「" + sect + "」，无需重复加入");
            }
            user.setSect(sect);
            userMapper.updateById(user);
        }
        return Result.ok("已加入「" + sect + "」，从此便是" + target.getGrade() + "宗门之人", target);
    }

    /**
     * 退出宗门
     */
    @PostMapping("/leave")
    public Result<?> leave(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("请先登录");
        }
        username = username.trim();
        User user = userMapper.selectByUsername(username);
        if (user == null || user.getSect() == null || user.getSect().isEmpty()) {
            return Result.fail("你尚未加入任何宗门");
        }
        String old = user.getSect();
        // 必须用 UpdateWrapper 显式置 null：MyBatis-Plus 的 updateById 默认跳过 null 字段
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getUsername, username)
                .set(User::getSect, null));
        return Result.ok("已退出「" + old + "」");
    }
}
