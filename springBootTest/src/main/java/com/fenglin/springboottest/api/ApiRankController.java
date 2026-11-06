package com.fenglin.springboottest.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.Douqi;
import com.fenglin.springboottest.entity.Fire;
import com.fenglin.springboottest.entity.Gongfa;
import com.fenglin.springboottest.entity.Pill;
import com.fenglin.springboottest.entity.Sect;
import com.fenglin.springboottest.mapper.DouqiMapper;
import com.fenglin.springboottest.mapper.FireMapper;
import com.fenglin.springboottest.mapper.GongfaMapper;
import com.fenglin.springboottest.mapper.PillMapper;
import com.fenglin.springboottest.mapper.SectMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 榜单类接口：把原本写在首页里的「异火榜 / 功法榜 / 丹药坊」抽成独立的排行榜页面数据源。
 *
 * 路径约定：
 *   GET /api/rank/fires   异火榜（按榜单名次升序，榜一在最前）
 *   GET /api/rank/gongfas 功法榜（按热度排序，榜一在最前）
 *   GET /api/rank/pills   丹药坊（按丹榜名次升序）
 * 响应统一为 { code, msg, data }，code = 0 表示成功。
 */
@RestController
@RequestMapping("/api/rank")
public class ApiRankController {

    @Resource
    private FireMapper fireMapper;

    @Resource
    private GongfaMapper gongfaMapper;

    @Resource
    private PillMapper pillMapper;

    @Resource
    private DouqiMapper douqiMapper;

    @Resource
    private SectMapper sectMapper;

    /**
     * 异火榜：按 sort（异火榜名次）升序，榜一在最前
     */
    @GetMapping("/fires")
    public Result<List<Fire>> fires() {
        List<Fire> list = fireMapper.selectList(
                new LambdaQueryWrapper<Fire>().orderByAsc(Fire::getSort));
        return Result.ok("异火榜已就绪", list);
    }

    /**
     * 功法榜：按 sort（热度排序）升序，榜一在最前
     */
    @GetMapping("/gongfas")
    public Result<List<Gongfa>> gongfas() {
        List<Gongfa> list = gongfaMapper.selectList(
                new LambdaQueryWrapper<Gongfa>().orderByAsc(Gongfa::getSort));
        return Result.ok("功法榜已就绪", list);
    }

    /**
     * 丹药坊：按 sort（丹榜名次）升序，榜一在最前
     */
    @GetMapping("/pills")
    public Result<List<Pill>> pills() {
        List<Pill> list = pillMapper.selectList(
                new LambdaQueryWrapper<Pill>().orderByAsc(Pill::getSort));
        return Result.ok("丹药坊已就绪", list);
    }

    /**
     * 斗气榜：按 sort（晋升顺序）升序，斗之气在最前
     */
    @GetMapping("/douqis")
    public Result<List<Douqi>> douqis() {
        List<Douqi> list = douqiMapper.selectList(
                new LambdaQueryWrapper<Douqi>().orderByAsc(Douqi::getSort));
        return Result.ok("斗气榜已就绪", list);
    }

    /**
     * 宗门：按 sort（势力榜名次）升序，榜一在最前
     */
    @GetMapping("/sects")
    public Result<List<Sect>> sects() {
        List<Sect> list = sectMapper.selectList(
                new LambdaQueryWrapper<Sect>().orderByAsc(Sect::getSort));
        return Result.ok("宗门已就绪", list);
    }
}
