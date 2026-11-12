package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.Admin;

/**
 * 后台管理员 Mapper，继承 MyBatis-Plus 的 BaseMapper 即拥有 CRUD 能力。
 * 由启动类上的 @MapperScan 统一扫描。
 */
public interface AdminMapper extends BaseMapper<Admin> {
}
