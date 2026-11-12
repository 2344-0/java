package com.fenglin.springboottest.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.Douqi;
import com.fenglin.springboottest.entity.FeedPost;
import com.fenglin.springboottest.entity.Fire;
import com.fenglin.springboottest.entity.Gongfa;
import com.fenglin.springboottest.entity.HotWord;
import com.fenglin.springboottest.entity.Pill;
import com.fenglin.springboottest.entity.Sect;
import com.fenglin.springboottest.mapper.DouqiMapper;
import com.fenglin.springboottest.mapper.FeedPostMapper;
import com.fenglin.springboottest.mapper.FireMapper;
import com.fenglin.springboottest.mapper.GongfaMapper;
import com.fenglin.springboottest.mapper.HotWordMapper;
import com.fenglin.springboottest.mapper.PillMapper;
import com.fenglin.springboottest.mapper.SectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理系统的通用数据接口：一套 CRUD 覆盖首页展示用到的 7 张表。
 *
 *   GET    /api/admin/data/{type}?kw=   列表（支持关键词模糊搜索）
 *   POST   /api/admin/data/{type}       新增
 *   PUT    /api/admin/data/{type}       修改
 *   DELETE /api/admin/data/{type}/{id} 删除
 *
 * type 取值：fires / gongfa / pills / douqis / sects / hotwords / feed
 * 所有接口都由 AdminAuthInterceptor 保护，必须带 X-Admin-Token。
 *
 * 这里用「注册表 + BaseMapper + Jackson 转实体」避免为 7 张表写 28 个几乎一样的接口；
 * 表名、排序字段、搜索列全部在服务端白名单里写死，不接受前端传列名，避免 SQL 注入。
 */
@RestController
@RequestMapping("/api/admin/data")
@SuppressWarnings({"rawtypes", "unchecked"})
public class AdminDataController {

    private final ObjectMapper objectMapper;

    /** key -> 资源配置（白名单） */
    private final Map<String, AdminResource> resources = new LinkedHashMap<>();

    public AdminDataController(ObjectMapper objectMapper,
                               FireMapper fireMapper,
                               GongfaMapper gongfaMapper,
                               PillMapper pillMapper,
                               DouqiMapper douqiMapper,
                               SectMapper sectMapper,
                               HotWordMapper hotWordMapper,
                               FeedPostMapper feedPostMapper) {
        this.objectMapper = objectMapper;

        register("fires", "异火图鉴", fireMapper, Fire.class, "name",
                new String[]{"name"}, "sort ASC, id ASC");
        register("gongfa", "热门功法", gongfaMapper, Gongfa.class, "name",
                new String[]{"name"}, "sort ASC, id ASC");
        register("pills", "丹药坊", pillMapper, Pill.class, "name",
                new String[]{"name"}, "sort ASC, id ASC");
        register("douqis", "斗气榜", douqiMapper, Douqi.class, "name",
                new String[]{"name"}, "sort ASC, id ASC");
        register("sects", "宗门势力", sectMapper, Sect.class, "name",
                new String[]{"name"}, "sort ASC, id ASC");
        register("hotwords", "热搜词", hotWordMapper, HotWord.class, "word",
                new String[]{"word"}, "sort ASC, id ASC");
        register("feed", "斗者动态", feedPostMapper, FeedPost.class, "author",
                new String[]{"author", "content"}, "created_at DESC, id DESC");
    }

    private void register(String key, String label, BaseMapper mapper, Class<?> entityType,
                          String primaryField, String[] likeColumns, String orderSql) {
        resources.put(key, new AdminResource(key, label, mapper, entityType,
                primaryField, likeColumns, orderSql));
    }

    private AdminResource require(String type) {
        return resources.get(type);
    }

    /** 列表：按各表约定的顺序返回，可按关键词搜索 */
    @GetMapping("/{type}")
    public Result<?> list(@PathVariable("type") String type,
                          @RequestParam(value = "kw", required = false) String kw) {
        AdminResource res = require(type);
        if (res == null) {
            return Result.fail("未知的数据模块：" + type);
        }

        QueryWrapper qw = new QueryWrapper();
        if (kw != null && !kw.trim().isEmpty()) {
            String like = kw.trim();
            for (int i = 0; i < res.likeColumns.length; i++) {
                if (i > 0) {
                    qw.or();
                }
                qw.like(res.likeColumns[i], like);
            }
        }
        qw.last("ORDER BY " + res.orderSql);

        List<?> list = res.mapper.selectList(qw);
        return Result.ok("已取得「" + res.label + "」共 " + list.size() + " 条", list);
    }

    /** 新增：id / 创建时间 / 更新时间一律交给数据库 */
    @PostMapping("/{type}")
    public Result<?> create(@PathVariable("type") String type,
                            @RequestBody Map<String, Object> body) {
        AdminResource res = require(type);
        if (res == null) {
            return Result.fail("未知的数据模块：" + type);
        }
        Map<String, Object> clean = cleanBody(body);
        clean.remove("id"); // 新增时主键由数据库自增
        Object primary = clean.get(res.primaryField);
        if (primary == null || primary.toString().trim().isEmpty()) {
            return Result.fail("「" + res.primaryField + "」不能为空");
        }

        Object entity = objectMapper.convertValue(clean, res.entityType);
        try {
            res.mapper.insert(entity);
        } catch (DuplicateKeyException e) {
            return Result.fail("新增失败：同名记录已存在");
        } catch (Exception e) {
            return Result.fail("新增失败：" + brief(e));
        }
        return Result.ok("已新增「" + primary + "」", entity);
    }

    /** 修改：body 必须带 id */
    @PutMapping("/{type}")
    public Result<?> update(@PathVariable("type") String type,
                            @RequestBody Map<String, Object> body) {
        AdminResource res = require(type);
        if (res == null) {
            return Result.fail("未知的数据模块：" + type);
        }
        Object idVal = body == null ? null : body.get("id");
        if (idVal == null || idVal.toString().trim().isEmpty()) {
            return Result.fail("缺少记录 ID，无法保存修改");
        }

        Map<String, Object> clean = cleanBody(body);
        Object entity = objectMapper.convertValue(clean, res.entityType);
        try {
            res.mapper.updateById(entity);
        } catch (DuplicateKeyException e) {
            return Result.fail("保存失败：同名记录已存在");
        } catch (Exception e) {
            return Result.fail("保存失败：" + brief(e));
        }
        return Result.ok("修改已保存", entity);
    }

    /** 删除 */
    @DeleteMapping("/{type}/{id}")
    public Result<?> delete(@PathVariable("type") String type,
                            @PathVariable("id") Long id) {
        AdminResource res = require(type);
        if (res == null) {
            return Result.fail("未知的数据模块：" + type);
        }
        try {
            res.mapper.deleteById(id);
        } catch (Exception e) {
            return Result.fail("删除失败：" + brief(e));
        }
        return Result.ok("记录已删除");
    }

    /** 剔除前端不该提交的字段，避免覆盖数据库维护的时间与主键 */
    private Map<String, Object> cleanBody(Map<String, Object> body) {
        Map<String, Object> clean = new LinkedHashMap<>();
        if (body != null) {
            clean.putAll(body);
        }
        clean.remove("createdAt");
        clean.remove("updatedAt");
        return clean;
    }

    private String brief(Exception e) {
        String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        return msg.length() > 120 ? msg.substring(0, 120) : msg;
    }

    /** 一张表的 CRUD 配置 */
    private static class AdminResource {
        private final String key;
        private final String label;
        private final BaseMapper mapper;
        private final Class<?> entityType;
        private final String primaryField;
        private final String[] likeColumns;
        private final String orderSql;

        AdminResource(String key, String label, BaseMapper mapper, Class<?> entityType,
                      String primaryField, String[] likeColumns, String orderSql) {
            this.key = key;
            this.label = label;
            this.mapper = mapper;
            this.entityType = entityType;
            this.primaryField = primaryField;
            this.likeColumns = likeColumns;
            this.orderSql = orderSql;
        }
    }
}
