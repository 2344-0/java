package com.fenglin.springboottest.service;

import com.fenglin.springboottest.entity.Admin;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后台管理员会话（演示级 Token 方案）。
 *
 * 登录成功后签发 token，之后所有 /api/admin/data/** 等管理接口都要带上
 * 请求头 X-Admin-Token，由 AdminAuthInterceptor 统一校验。
 * token 存在内存中，服务重启即失效；有效期 2 小时。
 * 生产环境请替换为 JWT / Redis 会话。
 */
@Service
public class AdminSession {

    /** 前端请求头名称 */
    public static final String HEADER = "X-Admin-Token";

    /** 会话有效期：2 小时 */
    private static final long TTL_MILLIS = 2 * 60 * 60 * 1000L;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    /**
     * 签发新会话
     */
    public String create(Admin admin) {
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, new Session(
                admin.getUsername(),
                admin.getNickname(),
                System.currentTimeMillis() + TTL_MILLIS));
        return token;
    }

    /**
     * 取会话信息，无效 / 过期返回 null
     */
    public Session get(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }
        if (session.expireAt < System.currentTimeMillis()) {
            sessions.remove(token);
            return null;
        }
        return session;
    }

    public boolean isValid(String token) {
        return get(token) != null;
    }

    /**
     * 退出登录：立即销毁 token
     */
    public void invalidate(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    /** 会话中保存的最小信息 */
    public static class Session {
        private final String username;
        private final String nickname;
        private final long expireAt;

        public Session(String username, String nickname, long expireAt) {
            this.username = username;
            this.nickname = nickname;
            this.expireAt = expireAt;
        }

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }

        public long getExpireAt() {
            return expireAt;
        }
    }
}
