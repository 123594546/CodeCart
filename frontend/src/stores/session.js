import { reactive } from 'vue'

import { getCurrentUser } from '@/api/auth'

const STORAGE_KEY = 'codecart-session'

export const sessionStore = reactive({
  token: '',
  user: null,
  ready: false,
  get isAuthenticated() {
    return Boolean(this.token && this.user)
  },
  get isAdmin() {
    return this.user?.roleCode === 'ADMIN'
  }
})

export function setSession(token, user) {
  sessionStore.token = token
  sessionStore.user = user
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ token, user }))
}

export function clearSession() {
  sessionStore.token = ''
  sessionStore.user = null
  localStorage.removeItem(STORAGE_KEY)
}

export async function bootstrapSession() {
  const cached = localStorage.getItem(STORAGE_KEY)
  if (!cached) {
    sessionStore.ready = true
    return
  }

  try {
    const parsed = JSON.parse(cached)
    sessionStore.token = parsed.token || ''
    sessionStore.user = parsed.user || null

    if (sessionStore.token) {
      sessionStore.user = await getCurrentUser()
      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({ token: sessionStore.token, user: sessionStore.user })
      )
    }
  } catch (error) {
    clearSession()
  } finally {
    sessionStore.ready = true
  }
}
