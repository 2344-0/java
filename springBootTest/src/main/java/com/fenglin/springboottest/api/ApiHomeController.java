package com.fenglin.springboottest.api;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.HomeDataVO;
import com.fenglin.springboottest.dto.SearchDataVO;
import com.fenglin.springboottest.service.HomeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供给 Vue 前端首页（HomePanel.vue）的 REST 接口。
 *
 * 路径约定：
 *   GET /api/home            首页聚合数据（热词 + 异火图鉴 + 热门功法 + 斗者动态）
 *   GET /api/home/search?kw= 搜索（同时命中异火 / 功法 / 斗者）
 * 响应统一为 { code, msg, data }，code = 0 表示成功。
 *
 * 注意：本类映射 /api/home，与 ApiUserController（/api/user）互不冲突。
 */
@RestController
@RequestMapping("/api/home")
public class ApiHomeController {

    @Resource
    private HomeService homeService;

    /**
     * 首页展示：一次返回首页所需的全部数据
     */
    @GetMapping
    public Result<HomeDataVO> home() {
        return Result.ok("首页数据已就绪", homeService.getHomeData());
    }

    /**
     * 首页搜索
     *
     * @param kw 搜索关键词（必填，可为空串返回空结果）
     */
    @GetMapping("/search")
    public Result<SearchDataVO> search(@RequestParam("kw") String kw) {
        SearchDataVO data = homeService.search(kw);
        if (data.getTotal() == 0) {
            return Result.ok("未寻得「" + (kw == null ? "" : kw) + "」，换个咒印再试", data);
        }
        return Result.ok("寻得 " + data.getTotal() + " 条相关记录", data);
    }
}
