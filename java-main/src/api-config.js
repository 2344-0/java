// ============================================================
// 斗破苍穹 · 后端接口地址 · 集中配置
// ------------------------------------------------------------
// 自动跟随当前访问地址：无论用 localhost、127.0.0.1 还是局域网 IP
// 打开页面，接口都指向「打开页面的那台机器」的 8081 端口。
// 换网络、路由器重新分配 IP 后，前端自动跟上，无需再改代码。
//
//   · 本机自测  ：http://localhost:5173     -> http://localhost:8081
//   · 局域网访问：http://192.168.68.66:5173 -> http://192.168.68.66:8081
//
// 需要临时指向别的后端时，在浏览器控制台执行：
//   localStorage.setItem('doupo_api_base', 'http://10.0.0.5:8081')
// 刷新即生效；执行 localStorage.removeItem('doupo_api_base') 可恢复自动推导。
// ============================================================

/** 后端端口（与 application-dev.properties 的 server.port 保持一致） */
const API_PORT = 8081

/** 兜底主机：浏览器拿不到 hostname 时使用（例如直接用 file:// 打开页面） */
const FALLBACK_HOST = 'localhost'

/**
 * 推导后端基础地址。
 * 优先使用 localStorage 中的手动覆盖值，其次跟随当前页面主机名。
 */
function resolveApiBase () {
  try {
    const override = localStorage.getItem('doupo_api_base')
    if (override) return override.replace(/\/+$/, '')
  } catch (e) {
    // 隐私模式下 localStorage 可能不可用，忽略并走自动推导
  }

  const host =
    (typeof window !== 'undefined' && window.location && window.location.hostname) ||
    FALLBACK_HOST

  return `http://${host}:${API_PORT}`
}

export const API_BASE = resolveApiBase()
