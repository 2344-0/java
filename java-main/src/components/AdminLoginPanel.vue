<template>
  <div class="panel fx d3 admin-login">
    <div class="kicker">斗 气 大 陆 · 数 据 中 枢</div>
    <h2><span class="spark"></span>后台执掌令</h2>
    <p class="desc">仅 限 管 理 员 登 入 · 无 注 册 通 道</p>

    <div class="badge">
      <svg viewBox="0 0 24 24"><path d="M12 2 4 6v6c0 5 3.5 8 8 10 4.5-2 8-5 8-10V6Z"/><path d="m9 12 2 2 4-4"/></svg>
      <span>此入口用于管理门户展示数据，与斗者账号互不相通</span>
    </div>

    <form class="admin-form" autocomplete="off" @submit.prevent="doLogin">
      <div class="field">
        <div class="box" :class="{ err: errors.username }">
          <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="3.6"/><path d="M4.6 20c.6-3.8 3.8-5.6 7.4-5.6S18.8 16.2 19.4 20"/></svg>
          <input v-model="form.username" type="text" placeholder="管理员账号" autocomplete="username" @input="clearErr('username')" />
        </div>
        <div class="msg" :class="{ show: errors.username }">{{ errors.username }}</div>
      </div>

      <div class="field">
        <div class="box" :class="{ err: errors.password }">
          <svg viewBox="0 0 24 24"><rect x="4.4" y="10.4" width="15.2" height="9.6" rx="2.6"/><path d="M8.2 10.4V7.8a3.8 3.8 0 0 1 7.6 0v2.6"/></svg>
          <input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="执掌秘钥（密码）" autocomplete="current-password" @input="clearErr('password')" />
          <button class="eye" type="button" :title="showPwd ? '隐藏密码' : '显示密码'" @click="showPwd = !showPwd">
            <svg viewBox="0 0 24 24"><path d="M2.6 12S6 6.2 12 6.2 21.4 12 21.4 12 18 17.8 12 17.8 2.6 12 2.6 12Z"/><circle cx="12" cy="12" r="2.8"/></svg>
          </button>
        </div>
        <div class="msg" :class="{ show: errors.password }">{{ errors.password }}</div>
      </div>

      <button class="submit" :class="{ loading }" type="submit" :disabled="loading">
        <span class="spin"></span>{{ loading ? '执 掌 令 校 验 中' : '进 入 后 台' }}
      </button>
    </form>

    <div class="tip">首次启动后端会自动创建默认管理员：<b>admin / admin123</b><br />请执行 sql/admin.sql 建表，正式使用前记得改密码。</div>
    <div class="switch-line"><a class="link" @click="$emit('back')">← 返回门户</a></div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { adminRequest, setSession } from '../admin/api'
import { showToast } from '../composables/useToast'

const emit = defineEmits(['login-success', 'back'])

const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })
const showPwd = ref(false)
const loading = ref(false)

function clearErr (key) {
  errors[key] = ''
}

async function doLogin () {
  errors.username = form.username.trim() ? '' : '请输入管理员账号'
  errors.password = form.password ? '' : '请输入执掌秘钥'
  if (errors.username || errors.password) return

  loading.value = true
  const res = await adminRequest('/login', {
    method: 'POST',
    body: JSON.stringify({ username: form.username.trim(), password: form.password })
  })
  loading.value = false

  if (res.code !== 0) {
    showToast(res.msg || '登录失败，请稍后再试', '✕')
    return
  }

  const user = { username: res.data.username, nickname: res.data.nickname }
  setSession(res.data.token, user)
  showToast('执掌令已验证，欢迎回来', '✦')
  emit('login-success', user)
}
</script>

<style scoped>
.admin-login .kicker {
  font-size: 11px;
  letter-spacing: .28em;
  color: var(--rune);
  opacity: .7;
  margin-bottom: 12px;
}
.admin-login .badge {
  display: flex;
  align-items: center;
  gap: 9px;
  margin: 24px 0 22px;
  padding: 11px 13px;
  border-radius: 12px;
  font-size: 11.5px;
  line-height: 1.6;
  letter-spacing: .04em;
  color: #b7aecb;
  background: rgba(255, 217, 138, .05);
  border: 1px solid var(--line);
}
.admin-login .badge svg {
  width: 17px;
  height: 17px;
  flex: none;
  stroke: var(--rune);
  fill: none;
  stroke-width: 1.6;
}
.admin-login .admin-form .submit {
  margin-top: 8px;
}
.admin-login .tip {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, .07);
  font-size: 11.5px;
  line-height: 1.9;
  color: #6f6982;
  text-align: center;
  letter-spacing: .03em;
}
.admin-login .tip b {
  color: var(--rune);
  font-weight: 400;
}
</style>
