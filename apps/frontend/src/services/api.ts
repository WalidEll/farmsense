import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api/v1'

const http: AxiosInstance = axios.create({ baseURL: BASE_URL })

// ── Request interceptor: attach access token ──────────────────
http.interceptors.request.use(config => {
  const token = localStorage.getItem('access_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ── Response interceptor: auto-refresh on 401 ─────────────────
let refreshing = false
let waitQueue: Array<{ resolve: (v: string) => void; reject: (e: unknown) => void }> = []

http.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config as AxiosRequestConfig & { _retry?: boolean }
    if (err.response?.status !== 401 || original._retry) {
      return Promise.reject(err)
    }
    original._retry = true

    if (refreshing) {
      return new Promise((resolve, reject) => {
        waitQueue.push({
          resolve: token => {
            if (original.headers) original.headers.Authorization = `Bearer ${token}`
            resolve(http(original))
          },
          reject
        })
      })
    }

    refreshing = true
    try {
      const refreshToken = localStorage.getItem('refresh_token')
      const { data } = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken })
      localStorage.setItem('access_token', data.accessToken)
      localStorage.setItem('refresh_token', data.refreshToken)
      waitQueue.forEach(q => q.resolve(data.accessToken))
      waitQueue = []
      if (original.headers) original.headers.Authorization = `Bearer ${data.accessToken}`
      return http(original)
    } catch (e) {
      waitQueue.forEach(q => q.reject(e))
      waitQueue = []
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      window.location.href = '/login'
      return Promise.reject(e)
    } finally {
      refreshing = false
    }
  }
)

// ── Typed wrappers ────────────────────────────────────────────
export const api = {
  get:    <T = unknown>(url: string, cfg?: AxiosRequestConfig) => http.get<T>(url, cfg).then(r => r.data),
  post:   <T = unknown>(url: string, data?: unknown)           => http.post<T>(url, data).then(r => r.data),
  put:    <T = unknown>(url: string, data?: unknown)           => http.put<T>(url, data).then(r => r.data),
  patch:  <T = unknown>(url: string, data?: unknown)           => http.patch<T>(url, data).then(r => r.data),
  delete: <T = unknown>(url: string)                           => http.delete<T>(url).then(r => r.data),
}

export default api
