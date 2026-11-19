import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 斗破苍穹登录页 · Vue 工程配置
export default defineConfig({
  plugins: [vue()],
  server: {
    // 本机局域网 IP：http://192.168.137.1:5173
    // 监听所有网卡（true / 0.0.0.0），保证 localhost 与 192.168.137.1 都能访问；
    // 若把 host 写死成单个 IP，本机 localhost:5173 反而会失效。
    host: true,
    port: 5173
  },
  build: {
    outDir: 'dist',
    assetsInlineLimit: 0 // 图片不内联，保持独立资源
  }
})
