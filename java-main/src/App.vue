<template>
  <div class="bg">
    <div class="stars"></div>
    <ParticleCanvas />
    <div class="vignette"></div>
  </div>

  <!-- 后台管理登录：独立页面，只有账号密码登录，没有注册入口 -->
  <div class="stage" v-if="view === 'admin-login'">
    <HeroSection />
    <section class="side">
      <AdminLoginPanel @login-success="onAdminLoginSuccess" @back="leaveAdmin" />
    </section>
  </div>

  <!-- 后台管理台：登录后进入，管理首页展示的数据 -->
  <AdminDashboard
    v-else-if="view === 'admin'"
    :admin="adminUser"
    @unauthorized="onAdminUnauthorized"
    @logout="onAdminLogout"
    @back-home="leaveAdmin"
  />

  <!-- 前台：登录 / 注册视图 -->
  <div class="stage" v-else-if="view !== 'home'">
    <HeroSection />
    <section class="side">
      <LoginPanel v-if="view === 'login'" @goto-register="view = 'register'" @login-success="onLoginSuccess" />
      <RegisterPanel v-else @goto-login="view = 'login'" />
    </section>
  </div>

  <!-- 前台首页：登录成功后进入 -->
  <HomePanel v-else :username="currentUser" @logout="onLogout" @goto-admin="gotoAdmin" />

  <ThemeSwitcher v-if="view !== 'admin'" />
  <ToastHost />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import ParticleCanvas from './components/ParticleCanvas.vue'
import HeroSection from './components/HeroSection.vue'
import ThemeSwitcher from './components/ThemeSwitcher.vue'
import ToastHost from './components/ToastHost.vue'
import LoginPanel from './components/LoginPanel.vue'
import RegisterPanel from './components/RegisterPanel.vue'
import HomePanel from './components/HomePanel.vue'
import AdminLoginPanel from './components/AdminLoginPanel.vue'
import AdminDashboard from './components/AdminDashboard.vue'
import { getToken, clearSession, getSessionUser } from './admin/api'
import { setTheme } from './composables/useTheme'
import { showToast } from './composables/useToast'

// 视图：login / register / home 为前台，admin-login / admin 为后台
const view = ref('login')
const currentUser = ref('')
const adminUser = ref(null)
// 记录进入后台前所在的前台视图，返回门户时还原
const returnView = ref('login')

function onLoginSuccess (payload) {
  currentUser.value = (payload && payload.username) || ''
  view.value = 'home'
}

function onLogout () {
  currentUser.value = ''
  view.value = 'login'
  showToast('已退出斗气，期待再度相逢', '✦')
}

/* ==================== 后台入口：极简 hash 路由 ====================
   项目没有引入 vue-router，这里用 #/admin/login、#/admin 两个 hash
   让后台页面可以直接访问、刷新不丢状态，又不影响原有的前台视图切换。 */
function setHash (hash) {
  if (hash) {
    if (window.location.hash !== hash) window.location.hash = hash
  } else if (window.location.hash) {
    window.history.replaceState(null, '', window.location.pathname + window.location.search)
  }
}

function viewByHash () {
  const h = window.location.hash
  if (h === '#/admin/login') return 'admin-login'
  if (h === '#/admin') return 'admin'
  return null
}

function gotoAdmin () {
  returnView.value = view.value === 'home' ? 'home' : 'login'
  view.value = 'admin-login'
  setHash('#/admin/login')
}

function onAdminLoginSuccess (user) {
  adminUser.value = user
  view.value = 'admin'
  setHash('#/admin')
}

function onAdminLogout () {
  clearSession()
  adminUser.value = null
  view.value = 'admin-login'
  setHash('#/admin/login')
  showToast('已退出后台管理', '✦')
}

// 管理接口返回 401（token 失效 / 被服务端清理）时，退回后台登录页
function onAdminUnauthorized () {
  clearSession()
  adminUser.value = null
  view.value = 'admin-login'
  setHash('#/admin/login')
  showToast('登录已失效，请重新登录', '✕')
}

function leaveAdmin () {
  view.value = returnView.value === 'home' ? 'home' : 'login'
  setHash('')
}

function onHashChange () {
  const target = viewByHash()
  if (target === 'admin') {
    if (!getToken()) {
      view.value = 'admin-login'
      setHash('#/admin/login')
      return
    }
    adminUser.value = getSessionUser()
    view.value = 'admin'
  } else if (target === 'admin-login') {
    view.value = 'admin-login'
  } else {
    view.value = returnView.value === 'home' ? 'home' : 'login'
  }
}

onMounted(() => {
  // 同步初始异火主题到 <body>，驱动 CSS 变量
  setTheme('yaohuo')

  const target = viewByHash()
  if (target === 'admin') {
    if (getToken()) {
      adminUser.value = getSessionUser()
      view.value = 'admin'
    } else {
      view.value = 'admin-login'
      setHash('#/admin/login')
    }
  } else if (target === 'admin-login') {
    view.value = 'admin-login'
  }
  window.addEventListener('hashchange', onHashChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('hashchange', onHashChange)
})
</script>
