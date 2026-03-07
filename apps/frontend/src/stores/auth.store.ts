import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import type { User, AuthResponse } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(localStorage.getItem('access_token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refresh_token'))

  const isLoggedIn = computed(() => !!accessToken.value)
  const lang = computed(() => user.value?.lang ?? 'FR')
  const isRtl = computed(() => ['AR', 'DARIJA'].includes(lang.value))

  async function register(name: string, email: string, password: string, lang: string, phoneWa?: string) {
    const res = await api.post<AuthResponse>('/auth/register', { name, email, password, lang, phoneWa })
    setTokens(res)
  }

  async function login(email: string, password: string) {
    const res = await api.post<AuthResponse>('/auth/login', { email, password })
    setTokens(res)
  }

  async function refresh() {
    if (!refreshToken.value) throw new Error('No refresh token')
    const res = await api.post<AuthResponse>('/auth/refresh', { refreshToken: refreshToken.value })
    setTokens(res)
  }

  function logout() {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
  }

  function setTokens(res: AuthResponse) {
    accessToken.value = res.accessToken
    refreshToken.value = res.refreshToken
    localStorage.setItem('access_token', res.accessToken)
    localStorage.setItem('refresh_token', res.refreshToken)
    // Map flat AuthResponse → User shape
    user.value = {
      userId: res.userId,
      name: res.name,
      email: res.email,
      lang: res.lang,
      phoneWa: res.phoneWa,
      latitude: res.latitude,
      longitude: res.longitude,
    }
  }

  const hasLocation = computed(() => user.value?.latitude != null && user.value?.longitude != null)

  async function updateLocation(latitude: number, longitude: number) {
    await api.put('/user/location', { latitude, longitude })
    if (user.value) {
      user.value.latitude = latitude
      user.value.longitude = longitude
    }
  }

  return { user, accessToken, refreshToken, isLoggedIn, lang, isRtl, hasLocation,
           register, login, refresh, logout, updateLocation }
})
