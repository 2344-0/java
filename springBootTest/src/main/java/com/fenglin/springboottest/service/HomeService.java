package com.fenglin.springboottest.service;

import com.fenglin.springboottest.dto.HomeDataVO;
import com.fenglin.springboottest.dto.SearchDataVO;

/**
 * 首页业务：首页聚合数据 + 全站搜索
 */
public interface HomeService {

    /**
     * 首页展示：一次返回热词、异火图鉴、热门功法、斗者动态
     */
    HomeDataVO getHomeData();

    /**
     * 首页搜索：同时检索异火（名称/关键词/简介）、功法（名称/简介）、斗者账号
     *
     * @param keyword 搜索关键词
     */
    SearchDataVO search(String keyword);
}
