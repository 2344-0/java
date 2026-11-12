// 后台管理系统 · 接口封装
// 后端地址与前端 HomePanel.vue 中写死的 8081 端口保持一致。
export const ADMIN_API = 'http://localhost:8081/api/admin'

const TOKEN_KEY = 'doupo_admin_token'
const USER_KEY = 'doupo_admin_user'

export function getToken () {
  return sessionStorage.getItem(TOKEN_KEY) || ''
}

export function getSessionUser () {
  try {
    return JSON.parse(sessionStorage.getItem(USER_KEY) || '{}')
  } catch (e) {
    return {}
  }
}

/** 登录成功后保存会话（sessionStorage：关闭标签页即失效） */
export function setSession (token, user) {
  sessionStorage.setItem(TOKEN_KEY, token || '')
  sessionStorage.setItem(USER_KEY, JSON.stringify(user || {}))
}

export function clearSession () {
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
}

/**
 * 统一请求：自动带上 X-Admin-Token。
 * 返回的永远是 { code, msg, data } 结构；code = 401 表示登录失效，由调用方跳回登录页。
 */
export async function adminRequest (path, options = {}) {
  const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {})
  const token = getToken()
  if (token) headers['X-Admin-Token'] = token

  let response
  try {
    response = await fetch(ADMIN_API + path, Object.assign({}, options, { headers }))
  } catch (e) {
    return { code: -1, msg: '无法连接后端服务（http://localhost:8081）', data: null }
  }

  try {
    return await response.json()
  } catch (e) {
    return { code: -1, msg: '后端响应异常（HTTP ' + response.status + '）', data: null }
  }
}
