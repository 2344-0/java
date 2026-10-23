package com.fenglin.springboottest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 注册请求体。
 * 前端 RegisterPanel.vue 提交的是 user / pwd / phone，
 * 这里用 @JsonProperty 做字段名映射，Java 侧仍使用可读的 username / password。
 */
public class RegisterDTO {

    @JsonProperty("user")
    private String username;

    @JsonProperty("pwd")
    private String password;

    private String phone;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
