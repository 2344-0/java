package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.HotWord;

/**
 * 热搜词 Mapper，继承 MyBatis-Plus 的 BaseMapper 即拥有 CRUD 能力
 */
public interface HotWordMapper extends BaseMapper<HotWord> {
}
