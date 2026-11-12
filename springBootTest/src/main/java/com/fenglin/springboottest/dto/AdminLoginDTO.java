package com.fenglin.springboottest.dto;

/**
 * 后台管理员登录请求体：{ username, password }。
 * 与前台 LoginDTO（user / pwd）分开，避免两套登录互相影响。
 */
public class AdminLoginDTO {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
