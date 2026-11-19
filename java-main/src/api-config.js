// ============================================================
// 斗破苍穹 · 后端接口地址 · 集中配置
// ------------------------------------------------------------
// 后端固定为本机局域网 IP：192.168.137.1 : 8081
// 前端所有接口（登录 / 注册 / 首页 / 排行榜 / 后台）统一走这个地址，
// 无论用 localhost、127.0.0.1 还是局域网 IP 打开页面都不变。
//
//   · 前端页面：http://192.168.137.1:5173
//   · 后端接口：http://192.168.137.1:8081
//
// 换机器 / 换 IP 时，只改下面 API_HOST 一处即可。
// 需要临时指向别的后端时，在浏览器控制台执行：
//   localStorage.setItem('doupo_api_base', 'http://10.0.0.5:8081')
// 刷新即生效；执行 localStorage.removeItem('doupo_api_base') 可恢复固定地址。
// ============================================================

/** 后端主机：本机局域网 IP */
const API_HOST = '192.168.137.1'

/** 后端端口（与 application-dev.properties 的 server.port 保持一致） */
const API_PORT = 8081

/** 固定的后端基础地址 */
const FIXED_API_BASE = `http://${API_HOST}:${API_PORT}`

/**
 * 推导后端基础地址。
 * 优先使用 localStorage 中的手动覆盖值，否则使用固定的本机 IP。
 */
function resolveApiBase () {
  try {
    const override = localStorage.getItem('doupo_api_base')
    if (override) return override.replace(/\/+$/, '')
  } catch (e) {
    // 隐私模式下 localStorage 可能不可用，忽略并使用固定地址
  }

  return FIXED_API_BASE
}

export const API_BASE = resolveApiBase()
