package com.fenglin.springboottest.dto;

import com.fenglin.springboottest.entity.Fire;
import com.fenglin.springboottest.entity.Gongfa;
import com.fenglin.springboottest.entity.Pill;

import java.util.List;

/**
 * 首页搜索结果 VO
 * 一次搜索同时命中：异火、功法、丹药、斗者（已注册用户）
 */
public class SearchDataVO {

    /** 搜索关键词 */
    private String keyword;

    /** 命中的异火列表 */
    private List<Fire> fires;

    /** 命中的功法列表 */
    private List<Gongfa> gongfa;

    /** 命中的丹药列表 */
    private List<Pill> pills;

    /** 命中的斗者账号（仅用户名，最多 10 个） */
    private List<String> users;

    /** 命中总数 */
    private int total;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Fire> getFires() {
        return fires;
    }

    public void setFires(List<Fire> fires) {
        this.fires = fires;
    }

    public List<Gongfa> getGongfa() {
        return gongfa;
    }

    public void setGongfa(List<Gongfa> gongfa) {
        this.gongfa = gongfa;
    }

    public List<Pill> getPills() {
        return pills;
    }

    public void setPills(List<Pill> pills) {
        this.pills = pills;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
