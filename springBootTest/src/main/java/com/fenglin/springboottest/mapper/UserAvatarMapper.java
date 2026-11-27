package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.UserAvatar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 斗者形象配置 Mapper，继承 MyBatis-Plus 的 BaseMapper 即拥有 CRUD 能力
 */
@Mapper
public interface UserAvatarMapper extends BaseMapper<UserAvatar> {

    /**
     * 按账号查询形象配置（账号唯一，最多一条）
     */
    @Select("SELECT * FROM t_user_avatar WHERE username = #{username} LIMIT 1")
    UserAvatar selectByUsername(String username);
}
