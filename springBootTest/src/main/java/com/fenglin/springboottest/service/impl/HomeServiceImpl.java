package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fenglin.springboottest.dto.FeedVO;
import com.fenglin.springboottest.dto.HomeDataVO;
import com.fenglin.springboottest.dto.SearchDataVO;
import com.fenglin.springboottest.entity.FeedPost;
import com.fenglin.springboottest.entity.Fire;
import com.fenglin.springboottest.entity.Gongfa;
import com.fenglin.springboottest.entity.HotWord;
import com.fenglin.springboottest.entity.Pill;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.FeedPostMapper;
import com.fenglin.springboottest.mapper.FireMapper;
import com.fenglin.springboottest.mapper.GongfaMapper;
import com.fenglin.springboottest.mapper.HotWordMapper;
import com.fenglin.springboottest.mapper.PillMapper;
import com.fenglin.springboottest.mapper.UserMapper;
import com.fenglin.springboottest.service.HomeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 首页业务实现：数据全部来自数据库，与前端 HomePanel.vue 的展示结构一一对应
 */
@Service
public class HomeServiceImpl implements HomeService {

    /** 斗者搜索最多返回条数 */
    private static final int USER_SEARCH_LIMIT = 10;

    @Resource
    private FireMapper fireMapper;

    @Resource
    private GongfaMapper gongfaMapper;

    @Resource
    private FeedPostMapper feedPostMapper;

    @Resource
    private HotWordMapper hotWordMapper;

    @Resource
    private PillMapper pillMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public HomeDataVO getHomeData() {
        HomeDataVO vo = new HomeDataVO();

        // 1. 热搜词：按 sort 升序
        List<HotWord> hotWords = hotWordMapper.selectList(
                new LambdaQueryWrapper<HotWord>().orderByAsc(HotWord::getSort));
        List<String> words = new ArrayList<>();
        for (HotWord hw : hotWords) {
            words.add(hw.getWord());
        }
        vo.setHotWords(words);

        // 2. 异火图鉴：按 sort 升序（sort 即异火榜名次，1 = 榜一）
        vo.setFires(fireMapper.selectList(
                new LambdaQueryWrapper<Fire>().orderByAsc(Fire::getSort)));

        // 3. 热门功法：按 sort 升序（sort 与热度排序一致）
        vo.setGongfa(gongfaMapper.selectList(
                new LambdaQueryWrapper<Gongfa>().orderByAsc(Gongfa::getSort)));

        // 4. 斗者动态：按发布时间倒序，并换算「片刻前 / 昨日」文案
        List<FeedPost> posts = feedPostMapper.selectList(
                new LambdaQueryWrapper<FeedPost>().orderByDesc(FeedPost::getCreatedAt));
        List<FeedVO> feed = new ArrayList<>();
        for (FeedPost p : posts) {
            feed.add(new FeedVO(p.getAuthor(), relativeTime(p.getCreatedAt()), p.getContent()));
        }
        vo.setFeed(feed);

        return vo;
    }

    @Override
    public SearchDataVO search(String keyword) {
        SearchDataVO vo = new SearchDataVO();
        String kw = keyword == null ? "" : keyword.trim();
        vo.setKeyword(kw);

        if (kw.isEmpty()) {
            vo.setFires(Collections.emptyList());
            vo.setGongfa(Collections.emptyList());
            vo.setPills(Collections.emptyList());
            vo.setUsers(Collections.emptyList());
            vo.setTotal(0);
            return vo;
        }

        String like = "%" + kw + "%";

        // 1. 搜异火：名称 / 关键词 / 简介任一命中，按榜次排序
        List<Fire> fires = fireMapper.selectList(
                new LambdaQueryWrapper<Fire>()
                        .and(w -> w.like(Fire::getName, like)
                                .or().like(Fire::getKeywords, like)
                                .or().like(Fire::getDescription, like))
                        .orderByAsc(Fire::getSort));
        vo.setFires(fires);

        // 2. 搜功法：名称 / 简介任一命中，按榜次排序
        List<Gongfa> gongfa = gongfaMapper.selectList(
                new LambdaQueryWrapper<Gongfa>()
                        .and(w -> w.like(Gongfa::getName, like)
                                .or().like(Gongfa::getDescription, like))
                        .orderByAsc(Gongfa::getSort));
        vo.setGongfa(gongfa);

        // 3. 搜丹药：名称 / 功效 / 主材 / 简介任一命中，按丹榜名次排序
        List<Pill> pills = pillMapper.selectList(
                new LambdaQueryWrapper<Pill>()
                        .and(w -> w.like(Pill::getName, like)
                                .or().like(Pill::getEffect, like)
                                .or().like(Pill::getMaterial, like)
                                .or().like(Pill::getDescription, like))
                        .orderByAsc(Pill::getSort));
        vo.setPills(pills);

        // 4. 搜斗者：账号模糊匹配，只返回用户名，最多 10 个
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .like(User::getUsername, like)
                        .orderByAsc(User::getId)
                        .last("LIMIT " + USER_SEARCH_LIMIT));
        List<String> names = new ArrayList<>();
        for (User u : users) {
            names.add(u.getUsername());
        }
        vo.setUsers(names);

        vo.setTotal(fires.size() + gongfa.size() + pills.size() + names.size());
        return vo;
    }

    /**
     * 把发布时间换算成前端展示用的相对时间文案
     */
    private String relativeTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        Duration d = Duration.between(time, LocalDateTime.now());
        long minutes = d.toMinutes();
        if (minutes < 15) {
            return "片刻前";
        }
        if (minutes < 60) {
            return minutes + " 分钟前";
        }
        long hours = d.toHours();
        if (hours < 6) {
            return hours + " 个时辰前";
        }
        if (hours < 24) {
            return "今晨";
        }
        if (hours < 48) {
            return "昨日";
        }
        return time.toLocalDate().toString();
    }
}
