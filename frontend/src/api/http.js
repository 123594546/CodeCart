import axios from 'axios'

import { clearSession, sessionStore } from '@/stores/session'

const http = axios.create({
  baseURL: '/api',
  timeout: 12000
})

http.interceptors.request.use((config) => {
  if (sessionStore.token) {
    config.headers.Authorization = `Bearer ${sessionStore.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && typeof payload.code !== 'undefined') {
      if (payload.code >= 200 && payload.code < 300) {
        return payload.data
      }
      const error = new Error(payload.message || '请求失败')
      error.code = payload.code
      throw error
    }
    return payload
  },
  (error) => {
    const payload = error.response?.data
    const message = payload?.message || error.message || '网络请求失败'
    const wrappedError = new Error(message)
    wrappedError.code = payload?.code || error.response?.status
    if (wrappedError.code === 401) {
      clearSession()
    }
    throw wrappedError
  }
)

export default http
