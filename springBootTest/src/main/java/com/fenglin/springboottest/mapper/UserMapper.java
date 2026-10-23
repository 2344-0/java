package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 斗者 Mapper，继承 MyBatis-Plus 的 BaseMapper 即拥有 CRUD 能力
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 按账号查询（账号唯一，最多一条）
     */
    @Select("SELECT * FROM t_user WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    /**
     * 按手机号查询（手机号唯一，最多一条）
     */
    @Select("SELECT * FROM t_user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(String phone);
}
