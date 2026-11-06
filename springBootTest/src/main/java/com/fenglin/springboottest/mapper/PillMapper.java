package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.Pill;

/**
 * 丹药 Mapper，继承 MyBatis-Plus 的 BaseMapper 即拥有 CRUD 能力
 */
public interface PillMapper extends BaseMapper<Pill> {
}
