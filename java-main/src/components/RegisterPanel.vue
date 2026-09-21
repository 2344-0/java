<template>
  <div class="panel fx d3">
    <h2><span class="spark"></span>立下契约 · 成为斗者</h2>
    <p class="desc">填 写 斗 气 印 记 · 点 燃 属 于 你 的 异 火</p>

    <div class="field">
      <div class="box" :class="{ err: errors.user }">
        <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="3.6"/><path d="M4.6 20c.6-3.8 3.8-5.6 7.4-5.6S18.8 16.2 19.4 20"/></svg>
        <input v-model="form.user" type="text" placeholder="斗者账号（至少 3 位）" autocomplete="username" @input="clearErr('user')" />
      </div>
      <div class="msg" :class="{ show: errors.user }">{{ errors.user }}</div>
    </div>

    <div class="field">
      <div class="box" :class="{ err: errors.pwd }">
        <svg viewBox="0 0 24 24"><rect x="4.4" y="10.4" width="15.2" height="9.6" rx="2.6"/><path d="M8.2 10.4V7.8a3.8 3.8 0 0 1 7.6 0v2.6"/></svg>
        <input v-model="form.pwd" :type="showPwd ? 'text' : 'password'" placeholder="斗气秘钥（密码，至少 6 位）" autocomplete="new-password" @input="onPwdInput" />
        <button class="eye" type="button" :title="showPwd ? '隐藏密码' : '显示密码'" @click="showPwd = !showPwd">
          <svg viewBox="0 0 24 24"><path d="M2.6 12S6 6.2 12 6.2 21.4 12 21.4 12 18 17.8 12 17.8 2.6 12 2.6 12Z"/><circle cx="12" cy="12" r="2.8"/></svg>
        </button>
      </div>
      <div class="msg" :class="{ show: errors.pwd }">{{ errors.pwd }}</div>
      <div class="strength"><i :style="{ width: strength.width, background: strength.color }"></i></div>
    </div>

    <div class="field">
      <div class="box" :class="{ err: errors.confirm }">
        <svg viewBox="0 0 24 24"><rect x="4.4" y="10.4" width="15.2" height="9.6" rx="2.6"/><path d="M8.2 10.4V7.8a3.8 3.8 0 0 1 7.6 0v2.6"/></svg>
        <input v-model="form.confirm" :type="showPwd ? 'text' : 'password'" placeholder="再次确认斗气秘钥" autocomplete="new-password" @input="clearErr('confirm')" />
      </div>
      <div class="msg" :class="{ show: errors.confirm }">{{ errors.confirm }}</div>
    </div>

    <div class="field">
      <div class="box" :class="{ err: errors.phone }">
        <svg viewBox="0 0 24 24"><path d="M8.2 2.4h7.6c1.2 0 2.1.9 2.1 2.1v15c0 1.2-.9 2.1-2.1 2.1H8.2c-1.2 0-2.1-.9-2.1-2.1v-15c0-1.2.9-2.1 2.1-2.1Z"/><path d="M10.6 18.4h2.8"/></svg>
        <input v-model="form.phone" type="tel" maxlength="11" placeholder="绑定手机号" autocomplete="tel" @input="clearErr('phone')" />
      </div>
      <div class="msg" :class="{ show: errors.phone }">{{ errors.phone }}</div>
    </div>

    <div class="cap-row">
      <div class="field">
        <div class="box" :class="{ err: errors.cap }">
          <svg viewBox="0 0 24 24"><path d="M9.4 5.2 4.6 18.8M14.6 5.2l4.8 13.6M7.4 14.4h9.2M6.2 9.6h11.6"/></svg>
          <input v-model="form.cap" type="text" maxlength="4" placeholder="咒印（验证码）" autocomplete="off" @input="clearErr('cap')" />
        </div>
        <div class="msg" :class="{ show: errors.cap }">{{ errors.cap }}</div>
      </div>
      <canvas id="captcha" ref="capCv" width="208" height="100" title="点击刷新" @click="drawCaptcha"></canvas>
    </div>

    <div class="opts">
      <label class="chk"><input type="checkbox" v-model="agree" /><span class="tick"></span>我已阅读并同意《斗气大陆契约》</label>
    </div>

    <button class="submit" :class="{ loading: loading, done: done }" type="button" :disabled="loading" @click="doRegister">
      <span class="spin"></span>{{ btnText }}
    </button>

    <div class="switch-line">已有斗者印记？<a class="link" @click="$emit('goto-login')">返回登录</a></div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { theme, getFireRGB } from '../composables/useTheme'
import { showToast } from '../composables/useToast'

const emit = defineEmits(['goto-login'])

const form = reactive({ user: '', pwd: '', confirm: '', phone: '', cap: '' })
const errors = reactive({ user: '', pwd: '', confirm: '', phone: '', cap: '' })
const showPwd = ref(false)
const agree = ref(false)
const loading = ref(false)
const done = ref(false)
const capCv = ref(null)
let capCode = ''
const POOL = 'ABCDEFGHJKLMNPQRSTUVWXY3456789'

const btnText = computed(() =>
  done.value ? '✦ 契 约 已 立' : loading.value ? '斗气凝聚中' : '立 契 注 册')

function toast (m, ic) { showToast(m, ic) }
function clearErr (f) { if (errors[f]) errors[f] = '' }
function setErr (f, m) { errors[f] = m }

const strength = computed(() => {
  const v = form.pwd
  let score = 0
  if (v.length >= 6) score++
  if (v.length >= 10) score++
  if (/[A-Z]/.test(v) && /[a-z]/.test(v)) score++
  if (/\d/.test(v) && /[^A-Za-z0-9]/.test(v)) score++
  const map = [
    { width: '0%', color: 'transparent' },
    { width: '25%', color: '#ff5163' },
    { width: '50%', color: '#ff8a2b' },
    { width: '75%', color: '#ffd479' },
    { width: '100%', color: '#3fbf7f' }
  ]
  return map[score]
})
function onPwdInput () {
  clearErr('pwd')
  if (form.confirm) clearErr('confirm')
}

function drawCaptcha () {
  const cv = capCv.value
  if (!cv) return
  const c = cv.getContext('2d')
  const fire = getFireRGB()
  capCode = ''
  const w = cv.width, h = cv.height
  c.clearRect(0, 0, w, h)
  const bg = c.createLinearGradient(0, 0, w, h)
  bg.addColorStop(0, '#181226'); bg.addColorStop(1, '#0b0814')
  c.fillStyle = bg; c.fillRect(0, 0, w, h)
  for (let i = 0; i < 7; i++) {
    c.strokeStyle = `rgba(${fire[0]},${fire[1]},${fire[2]},${0.10 + Math.random() * 0.2})`
    c.lineWidth = 1; c.beginPath()
    c.moveTo(Math.random() * w, Math.random() * h)
    c.bezierCurveTo(Math.random() * w, Math.random() * h, Math.random() * w, Math.random() * h, Math.random() * w, Math.random() * h)
    c.stroke()
  }
  for (let j = 0; j < 4; j++) {
    const ch = POOL[Math.floor(Math.random() * POOL.length)]
    capCode += ch
    c.save()
    c.translate(28 + j * 49 + (Math.random() * 6 - 3), h / 2 + (Math.random() * 10 - 5))
    c.rotate((Math.random() - 0.5) * 0.42)
    c.font = 'bold ' + (40 + Math.random() * 6).toFixed(0) + 'px Georgia, serif'
    c.textAlign = 'center'; c.textBaseline = 'middle'
    c.fillStyle = 'rgba(255,255,255,.9)'
    c.shadowColor = `rgba(${fire[0]},${fire[1]},${fire[2]},.9)`
    c.shadowBlur = 14
    c.fillText(ch, 0, 0)
    c.restore()
  }
  for (let m = 0; m < 42; m++) {
    c.fillStyle = 'rgba(255,255,255,' + (Math.random() * 0.32) + ')'
    c.fillRect(Math.random() * w, Math.random() * h, 1.6, 1.6)
  }
}

function validate () {
  if (form.user.trim().length < 3) { setErr('user', '账号至少 3 个字符'); return false }
  if (form.pwd.length < 6) { setErr('pwd', '密码不得少于 6 位'); return false }
  if (form.pwd !== form.confirm) { setErr('confirm', '两次秘钥不一致'); return false }
  if (!/^1[3-9]\d{9}$/.test(form.phone.trim())) { setErr('phone', '请输入正确的 11 位手机号'); return false }
  if (form.cap.trim().toUpperCase() !== capCode) { setErr('cap', '咒印错误，请重新识别'); drawCaptcha(); return false }
  if (!agree.value) { toast('请先同意《斗气大陆契约》'); return false }
  return true
}

function doRegister () {
  if (!validate()) return
  loading.value = true
  setTimeout(() => {
    loading.value = false; done.value = true
    toast('契约已成，异火待燃，欢迎加入斗气大陆！', '✦')
    setTimeout(() => { done.value = false }, 2200)
  }, 1500)
}

watch(theme, () => { drawCaptcha() })

onMounted(() => { drawCaptcha() })
onBeforeUnmount(() => {})
</script>
