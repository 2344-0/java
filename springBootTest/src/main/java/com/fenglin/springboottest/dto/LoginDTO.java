package com.fenglin.springboottest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 账号登录请求体。
 * 前端 LoginPanel.vue（账号登录）提交的是 user / pwd。
 */
public class LoginDTO {

    @JsonProperty("user")
    private String username;

    @JsonProperty("pwd")
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
