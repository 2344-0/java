<template>
  <div class="bg">
    <div class="stars"></div>
    <ParticleCanvas />
    <div class="vignette"></div>
  </div>

  <div class="stage">
    <HeroSection />
    <section class="side">
      <LoginPanel v-if="view === 'login'" @goto-register="view = 'register'" />
      <RegisterPanel v-else @goto-login="view = 'login'" />
    </section>
  </div>

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
import { setTheme } from './composables/useTheme'

const view = ref('login')

onMounted(() => {
  // 同步初始异火主题到 <body>，驱动 CSS 变量
  setTheme('yaohuo')
})
</script>
