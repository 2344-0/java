import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 斗破苍穹登录页 · Vue 工程配置
export default defineConfig({
  plugins: [vue()],
  server: {
    host: true,
    port: 5173
  },
  build: {
    outDir: 'dist',
    assetsInlineLimit: 0 // 图片不内联，保持独立资源
  }
})
