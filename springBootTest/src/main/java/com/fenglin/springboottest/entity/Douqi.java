package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 斗气境界实体，对应数据库表 t_douqi
 * 用于「斗气榜」页面展示（修炼等级晋升之路，从斗之气到斗帝）
 */
@TableName("t_douqi")
public class Douqi {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 境界名称（唯一），如：斗者 / 斗帝 */
    private String name;

    /** 阶位特征描述，如：内视识海，斗气成旋 */
    private String tierDesc;

    /** 境界简介 */
    private String description;

    /** 细分层级，如：一至九段 / 五星斗尊 */
    private String subLevel;

    /** 排序值，越小越靠前（对应晋升顺序，1 = 斗之气） */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTierDesc() {
        return tierDesc;
    }

    public void setTierDesc(String tierDesc) {
        this.tierDesc = tierDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubLevel() {
        return subLevel;
    }

    public void setSubLevel(String subLevel) {
        this.subLevel = subLevel;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
