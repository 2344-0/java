package com.fenglin.springboottest.dto;

import com.fenglin.springboottest.entity.Fire;
import com.fenglin.springboottest.entity.Gongfa;

import java.util.List;

/**
 * 首页聚合数据 VO：一次请求返回首页所需的全部展示数据
 */
public class HomeDataVO {

    /** 热搜词列表（搜索框下方「热门：」） */
    private List<String> hotWords;

    /** 异火图鉴（按异火榜名次排序） */
    private List<Fire> fires;

    /** 热门功法（按热度排序） */
    private List<Gongfa> gongfa;

    /** 斗者动态（按发布时间倒序） */
    private List<FeedVO> feed;

    public List<String> getHotWords() {
        return hotWords;
    }

    public void setHotWords(List<String> hotWords) {
        this.hotWords = hotWords;
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

    public List<FeedVO> getFeed() {
        return feed;
    }

    public void setFeed(List<FeedVO> feed) {
        this.feed = feed;
    }
}
