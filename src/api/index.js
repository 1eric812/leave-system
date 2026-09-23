import axios from 'axios'

// API 基础地址：
//  - 本地开发：走 Vite 代理（/api -> http://localhost:8080）
//  - 线上部署：通过 VITE_API_BASE_URL 注入后端公网地址
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request = axios.create({
  baseURL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const msg = err.response?.data?.message || err.message || '网络异常'
    console.error('[API Error]', msg)
    return Promise.reject(new Error(msg))
  }
)

export default request
