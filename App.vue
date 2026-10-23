<template>
  <div class="bg">
    <div class="stars"></div>
    <ParticleCanvas />
    <div class="vignette"></div>
  </div>

  <!-- 登录 / 注册视图 -->
  <div class="stage" v-if="view !== 'home'">
    <HeroSection />
    <section class="side">
      <LoginPanel v-if="view === 'login'" @goto-register="view = 'register'" @login-success="onLoginSuccess" />
      <RegisterPanel v-else @goto-login="view = 'login'" />
    </section>
  </div>

  <!-- 首页：登录成功后进入 -->
  <HomePanel v-else :username="currentUser" @logout="onLogout" />

  <ThemeSwitcher />
  <ToastHost />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ParticleCanvas from './components/ParticleCanvas.vue'
import HeroSection from './components/HeroSection.vue'
import ThemeSwitcher from './components/ThemeSwitcher.vue'
import ToastHost from './components/ToastHost.vue'
import LoginPanel from './components/LoginPanel.vue'
import RegisterPanel from './components/RegisterPanel.vue'
import HomePanel from './components/HomePanel.vue'
import { setTheme } from './composables/useTheme'
import { showToast } from './composables/useToast'

const view = ref('login')
const currentUser = ref('')

// 登录成功：取后端返回的账号名，切换到首页
function onLoginSuccess (payload) {
  currentUser.value = (payload && payload.username) || ''
  view.value = 'home'
}

// 退出斗气：清空登录态，回到登录页
function onLogout () {
  currentUser.value = ''
  view.value = 'login'
  showToast('已退出斗气，期待再度相逢', '✦')
}

onMounted(() => {
  // 同步初始异火主题到 <body>，驱动 CSS 变量
  setTheme('yaohuo')
})
</script>
