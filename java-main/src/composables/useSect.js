// ============================================================
// 宗门共享状态：当前斗者所属宗门 + 各宗门成员数
// ------------------------------------------------------------
// 与后端 /api/sect/* 接口联动，localStorage 仅作缓存与降级：
// 后端不可用时沿用本地缓存，保证页面始终可用。
// 存档 key：doupo_sect_v1
// ============================================================
import { ref } from 'vue'
import { API_BASE } from '../api-config'
import { showToast } from './useToast'

const SAVE_KEY = 'doupo_sect_v1'

function loadLocal () {
  try {
    const raw = localStorage.getItem(SAVE_KEY)
    if (raw) {
      const obj = JSON.parse(raw)
      return { sect: obj.sect || '', members: obj.members || {} }
    }
  } catch (e) {
    // 忽略损坏的缓存
  }
  return { sect: '', members: {} }
}

const initial = loadLocal()

/** 当前所属宗门名称（空串表示未加入） */
export const joinedSect = ref(initial.sect)

/** 各宗门成员数：{ 宗门名: 人数 } */
export const memberCounts = ref(initial.members)

/** 是否正在提交加入/退出 */
export const acting = ref(false)

function persist () {
  try {
    localStorage.setItem(SAVE_KEY, JSON.stringify({
      sect: joinedSect.value,
      members: memberCounts.value
    }))
  } catch (e) {
    // 隐私模式下忽略
  }
}

async function request (path, options) {
  const res = await fetch(API_BASE + path, options)
  return res.json()
}

/** 刷新当前用户的所属宗门 */
export async function loadMySect (username) {
  if (!username) {
    joinedSect.value = ''
    persist()
    return
  }
  try {
    const res = await request('/api/sect/mine?username=' + encodeURIComponent(username))
    if (res && res.code === 0 && res.data) {
      joinedSect.value = res.data.sect || ''
      persist()
    }
  } catch (e) {
    // 后端不可用时保留本地缓存
  }
}

/** 刷新各宗门成员数 */
export async function loadMembers () {
  try {
    const res = await request('/api/sect/members')
    if (res && res.code === 0 && res.data) {
      memberCounts.value = res.data
      persist()
    }
  } catch (e) {
    // 后端不可用时保留本地缓存
  }
}

/** 加入宗门 */
export async function joinSect (username, sect) {
  if (!username) {
    showToast('请先登录再加入宗门', '✕')
    return false
  }
  acting.value = true
  try {
    const res = await request('/api/sect/join', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, sect })
    })
    if (res && res.code === 0) {
      joinedSect.value = sect
      showToast(res.msg || ('已加入「' + sect + '」'), '✦')
      await loadMembers()
      persist()
      return true
    }
    showToast(res && res.msg ? res.msg : '加入宗门失败', '✕')
    return false
  } catch (e) {
    showToast(`后端服务未启动（${API_BASE}）`, '✕')
    return false
  } finally {
    acting.value = false
  }
}

/** 退出宗门 */
export async function leaveSect (username) {
  if (!username) {
    showToast('请先登录', '✕')
    return false
  }
  acting.value = true
  try {
    const res = await request('/api/sect/leave', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username })
    })
    if (res && res.code === 0) {
      joinedSect.value = ''
      showToast(res.msg || '已退出宗门', '✦')
      await loadMembers()
      persist()
      return true
    }
    showToast(res && res.msg ? res.msg : '退出宗门失败', '✕')
    return false
  } catch (e) {
    showToast(`后端服务未启动（${API_BASE}）`, '✕')
    return false
  } finally {
    acting.value = false
  }
}
