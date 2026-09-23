import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
// BASE_PATH：部署到 GitHub Pages（子路径 /leave-system/）时由 CI 注入，
// 本地开发留空即可，保持默认根路径。
export default defineConfig({
  base: process.env.BASE_PATH || '/',
  plugins: [vue()],
  build: {
    target: 'esnext',
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
