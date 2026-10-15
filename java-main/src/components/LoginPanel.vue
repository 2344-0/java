<template>
  <div class="panel fx d3">
    <h2><span class="spark"></span>登入斗气大陆</h2>
    <p class="desc">唤 醒 你 的 斗 气 · 点 燃 属 于 你 的 异 火</p>

    <div class="tabs" id="tabs">
      <button
        v-for="(t, i) in tabNames"
        :key="t"
        :ref="el => (tabEls[i] = el)"
        :class="{ on: activeTab === i }"
        type="button"
        @click="selectTab(i)"
      >{{ t }}</button>
      <div class="tab-bar" :style="tabBarStyle"></div>
    </div>

    <div class="panes">
      <!-- 账号登录 -->
      <div class="pane" :class="{ on: activeTab === 0 }">
        <div class="field">
          <div class="box" :class="{ err: errors.user }">
            <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="3.6"/><path d="M4.6 20c.6-3.8 3.8-5.6 7.4-5.6S18.8 16.2 19.4 20"/></svg>
            <input v-model="form.user" type="text" placeholder="斗者账号 / 手机号 / 邮箱" autocomplete="username" @input="clearErr('user')" />
          </div>
          <div class="msg" :class="{ show: errors.user }">{{ errors.user }}</div>
        </div>
        <div class="field">
          <div class="box" :class="{ err: errors.pwd }">
            <svg viewBox="0 0 24 24"><rect x="4.4" y="10.4" width="15.2" height="9.6" rx="2.6"/><path d="M8.2 10.4V7.8a3.8 3.8 0 0 1 7.6 0v2.6"/></svg>
            <input v-model="form.pwd" :type="showPwd ? 'text' : 'password'" placeholder="斗气秘钥（密码）" autocomplete="current-password" @input="clearErr('pwd')" />
            <button class="eye" type="button" :title="showPwd ? '隐藏密码' : '显示密码'" @click="showPwd = !showPwd">
              <svg viewBox="0 0 24 24"><path d="M2.6 12S6 6.2 12 6.2 21.4 12 21.4 12 18 17.8 12 17.8 2.6 12 2.6 12Z"/><circle cx="12" cy="12" r="2.8"/></svg>
            </button>
          </div>
          <div class="msg" :class="{ show: errors.pwd }">{{ errors.pwd }}</div>
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
          <label class="chk"><input type="checkbox" v-model="remember" checked /><span class="tick"></span>记住此间斗气</label>
          <a class="link" @click="toast('已发送秘钥重置符文至绑定账号')">忘记秘钥？</a>
        </div>
        <button class="submit" :class="{ loading: loginLoading, done: loginDone }" type="button" :disabled="loginLoading" @click="doLogin('account')">
          <span class="spin"></span>{{ loginBtnText }}
        </button>
        <div class="divider">其 他 方 式</div>
        <div class="quick">
          <button type="button" data-q="微信" title="微信登录" @click="toast('微信 授权登录中……')">
            <svg viewBox="0 0 24 24"><path d="M9.1 3.3C5.3 3.3 2.2 6 2.2 9.3c0 1.9 1 3.5 2.7 4.7l-.7 2.1 2.4-1.2c.8.2 1.6.3 2.5.3h.4c-.1-.4-.2-.8-.2-1.2 0-3.2 3.1-5.8 6.9-5.8h.3c-.6-2.8-3.3-4.9-6.4-4.9Zm-2.4 3a1 1 0 1 1 0 2 1 1 0 0 1 0-2Zm4.8 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2Z"/><path d="M22.1 14c0-2.7-2.6-4.9-5.9-4.9S10.3 11.3 10.3 14s2.6 4.9 5.9 4.9c.7 0 1.4-.1 2-.3l2 1-.5-1.7c1.5-1 2.4-2.4 2.4-3.9Zm-7.8-1.5a.9.9 0 1 1 0 1.8.9.9 0 0 1 0-1.8Zm4 0a.9.9 0 1 1 0 1.8.9.9 0 0 1 0-1.8Z"/></svg>
          </button>
          <button type="button" data-q="QQ" title="QQ 登录" @click="toast('QQ 授权登录中……')">
            <svg viewBox="0 0 24 24"><path d="M12 2.2c-3.4 0-5.6 2.6-5.6 5.9 0 .6-.4 1.2-.8 1.9-.7 1.1-1.6 2.4-1.6 4.4 0 .9.2 1.5.5 2-.3.5-.6 1.1-.6 1.6 0 .6.4.9 1.3.9.6 0 1.2-.2 1.7-.5.9.8 2.3 1.3 4.1 1.3s3.2-.5 4.1-1.3c.5.3 1.1.5 1.7.5.9 0 1.3-.3 1.3-.9 0-.5-.3-1.1-.6-1.6.3-.5.5-1.1.5-2 0-2-.9-3.3-1.6-4.4-.4-.7-.8-1.3-.8-1.9 0-3.3-2.2-5.9-5.6-5.9Zm-2 6.3c.6 0 1 .7 1 1.5s-.4 1.5-1 1.5-1-.7-1-1.5.4-1.5 1-1.5Zm4 0c.6 0 1 .7 1 1.5s-.4 1.5-1 1.5-1-.7-1-1.5.4-1.5 1-1.5Z"/></svg>
          </button>
          <button type="button" data-q="手机号" title="手机号登录" @click="toast('手机号 授权登录中……')">
            <svg viewBox="0 0 24 24"><path d="M8.2 2.4h7.6c1.2 0 2.1.9 2.1 2.1v15c0 1.2-.9 2.1-2.1 2.1H8.2c-1.2 0-2.1-.9-2.1-2.1v-15c0-1.2.9-2.1 2.1-2.1Zm2.2 2.6h3.2v1.1h-3.2V5Zm1.6 13.4a1.15 1.15 0 1 0 0-2.3 1.15 1.15 0 0 0 0 2.3Z"/></svg>
          </button>
        </div>
      </div>

      <!-- 手机验证 -->
      <div class="pane" :class="{ on: activeTab === 1 }">
        <div class="field">
          <div class="box" :class="{ err: errors.phone }">
            <svg viewBox="0 0 24 24"><path d="M8.2 2.4h7.6c1.2 0 2.1.9 2.1 2.1v15c0 1.2-.9 2.1-2.1 2.1H8.2c-1.2 0-2.1-.9-2.1-2.1v-15c0-1.2.9-2.1 2.1-2.1Z"/><path d="M10.6 18.4h2.8"/></svg>
            <input v-model="form.phone" type="tel" maxlength="11" placeholder="请输入手机号" autocomplete="tel" @input="clearErr('phone')" />
          </div>
          <div class="msg" :class="{ show: errors.phone }">{{ errors.phone }}</div>
        </div>
        <div class="field">
          <div class="box" :class="{ err: errors.code }">
            <svg viewBox="0 0 24 24"><path d="M6.6 10.6V8.2a5.4 5.4 0 0 1 10.8 0v2.4"/><rect x="4.4" y="10.6" width="15.2" height="9.4" rx="2.6"/></svg>
            <input v-model="form.code" type="text" maxlength="6" placeholder="斗气符文（6 位验证码）" inputmode="numeric" @input="clearErr('code')" />
            <button class="send" id="btnSend" type="button" :disabled="sendDisabled" @click="sendCode">{{ sendText }}</button>
          </div>
          <div class="msg" :class="{ show: errors.code }">{{ errors.code }}</div>
        </div>
        <div class="opts">
          <label class="chk"><input type="checkbox" v-model="agree" checked /><span class="tick"></span>同意《斗气大陆契约》</label>
        </div>
        <button class="submit" :class="{ loading: smsLoading, done: smsDone }" type="button" :disabled="smsLoading" @click="doLogin('sms')">
          <span class="spin"></span>{{ smsBtnText }}
        </button>
        <div class="foot">新入大陆？<a class="link" @click="$emit('goto-register')">立下契约，成为斗者</a></div>
      </div>

      <!-- 扫码登录 -->
      <div class="pane" :class="{ on: activeTab === 2 }">
        <div class="qr-wrap">
          <div class="qr-frame"><canvas id="qr" ref="qrCv" width="344" height="344"></canvas></div>
          <p>请使用 <b>斗气大陆</b> 客户端扫描<br />扫码即视为同意《斗气大陆契约》</p>
        </div>
        <div class="foot" style="margin-top:14px;"><a class="link" @click="drawQR(); toast('二维码已刷新')">二维码失效？点击刷新</a></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { theme, getFireRGB } from '../composables/useTheme'
import { showToast } from '../composables/useToast'

const emit = defineEmits(['goto-register'])

const tabNames = ['账号登录', '手机验证', '扫码登录']
const activeTab = ref(0)
const tabEls = []
const tabBarStyle = reactive({ left: '0px', width: '0px' })

const form = reactive({ user: '', pwd: '', cap: '', phone: '', code: '' })
const errors = reactive({ user: '', pwd: '', cap: '', phone: '', code: '' })
const showPwd = ref(false)
const remember = ref(true)
const agree = ref(true)

const loginLoading = ref(false)
const loginDone = ref(false)
const smsLoading = ref(false)
const smsDone = ref(false)

const sendText = ref('获取验证码')
const sendDisabled = ref(false)
let sendTimer = null

const capCv = ref(null)
const qrCv = ref(null)
let capCode = ''
const POOL = 'ABCDEFGHJKLMNPQRSTUVWXY3456789'

const loginBtnText = computed(() =>
  loginDone.value ? '✦ 斗 气 已 引 动' : loginLoading.value ? '斗气凝聚中' : '登 入')
const smsBtnText = computed(() =>
  smsDone.value ? '✦ 斗 气 已 引 动' : smsLoading.value ? '符 文 校 验' : '验 证 登 入')

function toast (m, ic) { showToast(m, ic) }
function clearErr (f) { if (errors[f]) errors[f] = '' }
function setErr (f, m) { errors[f] = m }

function moveBar (i) {
  const b = tabEls[i]
  if (!b) return
  tabBarStyle.left = b.offsetLeft + 'px'
  tabBarStyle.width = b.offsetWidth + 'px'
}
function selectTab (i) {
  activeTab.value = i
  nextTick(() => moveBar(i))
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

function drawQR () {
  const cv = qrCv.value
  if (!cv) return
  const c = cv.getContext('2d')
  const n = 29, s = cv.width / n
  c.clearRect(0, 0, cv.width, cv.height)
  c.fillStyle = '#f4f0e6'; c.fillRect(0, 0, cv.width, cv.height)
  c.fillStyle = '#0b0d14'
  let seed = 20260917
  const rnd = () => { seed = (seed * 1103515245 + 12345) & 0x7fffffff; return seed / 0x7fffffff }
  for (let y = 0; y < n; y++) {
    for (let x = 0; x < n; x++) {
      const reserve = (x < 8 && y < 8) || (x > n - 9 && y < 8) || (x < 8 && y > n - 9)
      if (reserve) continue
      if (rnd() > 0.52) c.fillRect(x * s, y * s, s, s)
    }
  }
  const eye = (ox, oy) => {
    c.fillStyle = '#0b0d14'; c.fillRect(ox * s, oy * s, 7 * s, 7 * s)
    c.fillStyle = '#f4f0e6'; c.fillRect((ox + 1) * s, (oy + 1) * s, 5 * s, 5 * s)
    c.fillStyle = '#0b0d14'; c.fillRect((ox + 2) * s, (oy + 2) * s, 3 * s, 3 * s)
    c.fillStyle = 'rgba(169,123,255,.85)'; c.fillRect((ox + 1) * s, (oy + 1) * s, 5 * s, 0.7 * s)
  }
  eye(0, 0); eye(n - 7, 0); eye(0, n - 7)
  c.save()
  c.translate(cv.width / 2, cv.height / 2)
  c.fillStyle = '#f4f0e6'
  c.beginPath(); c.arc(0, 0, 3.1 * s, 0, Math.PI * 2); c.fill()
  c.fillStyle = '#7b3dff'
  c.beginPath()
  for (let pt = 0; pt < 8; pt++) {
    const ang = pt * Math.PI / 4
    const rr = pt % 2 === 0 ? 2.3 * s : 1.15 * s
    const px = Math.cos(ang - Math.PI / 2) * rr
    const py = Math.sin(ang - Math.PI / 2) * rr
    if (pt === 0) c.moveTo(px, py); else c.lineTo(px, py)
  }
  c.closePath(); c.fill()
  c.restore()
}

function validate (ids) {
  for (const id of ids) {
    const v = form[id].trim()
    if (id === 'user' && v.length < 3) { setErr('user', '账号至少 3 个字符'); return false }
    if (id === 'pwd' && v.length < 6) { setErr('pwd', '密码不得少于 6 位'); return false }
    if (id === 'cap' && v.toUpperCase() !== capCode) { setErr('cap', '咒印错误，请重新识别'); drawCaptcha(); return false }
    if (id === 'phone' && !/^1[3-9]\d{9}$/.test(v)) { setErr('phone', '请输入正确的 11 位手机号'); return false }
    if (id === 'code' && !/^\d{6}$/.test(v)) { setErr('code', '验证码为 6 位数字'); return false }
  }
  return true
}

function doLogin (pane) {
  if (pane === 'account') {
    if (!validate(['user', 'pwd', 'cap'])) return
    loginLoading.value = true
    setTimeout(() => {
      loginLoading.value = false; loginDone.value = true
      toast('登录成功，异火认主，斗气涌入经脉……', '✦')
      setTimeout(() => { loginDone.value = false }, 2200)
    }, 1500)
  } else {
    if (!validate(['phone', 'code'])) return
    smsLoading.value = true
    setTimeout(() => {
      smsLoading.value = false; smsDone.value = true
      toast('验证通过，欢迎回来，斗者。', '✦')
      setTimeout(() => { smsDone.value = false }, 2200)
    }, 1500)
  }
}

function sendCode () {
  if (!/^1[3-9]\d{9}$/.test(form.phone.trim())) {
    setErr('phone', '请输入正确的 11 位手机号')
    return
  }
  let n = 60
  sendDisabled.value = true
  sendText.value = n + 's 后重发'
  toast('符文已送达，请留意斗气传讯')
  sendTimer = setInterval(() => {
    n--
    if (n <= 0) { clearInterval(sendTimer); sendDisabled.value = false; sendText.value = '获取验证码' }
    else sendText.value = n + 's 后重发'
  }, 1000)
}

function onKeydown (e) {
  if (e.key !== 'Enter') return
  if (activeTab.value === 0) doLogin('account')
  else if (activeTab.value === 1) doLogin('sms')
}
function onResize () { moveBar(activeTab.value) }

watch(theme, () => { drawCaptcha() })

onMounted(() => {
  drawCaptcha()
  drawQR()
  nextTick(() => moveBar(0))
  window.addEventListener('resize', onResize)
  window.addEventListener('keydown', onKeydown)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  window.removeEventListener('keydown', onKeydown)
  clearInterval(sendTimer)
})
</script>
