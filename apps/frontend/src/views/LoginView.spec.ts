import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginView from './LoginView.vue'
import { createPinia, setActivePinia } from 'pinia'

// Mock i18n
vi.mock('@/i18n', () => ({
  useI18n: () => ({
    t: (key: string) => key.replace(/\./g, '_')
  })
}))

// Mock router
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush
  }),
  RouterLink: {
    template: '<a><slot /></a>'
  }
}))

// Mock auth store
const mockLogin = vi.fn()
vi.mock('@/stores/auth.store', () => ({
  useAuthStore: () => ({
    login: mockLogin
  })
}))

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders login form', () => {
    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          RouterLink: { template: '<a><slot /></a>' }
        }
      }
    })
    expect(wrapper.find('form').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').text()).toBe('auth_login')
  })

  it('calls auth.login and redirects on success', async () => {
    mockLogin.mockResolvedValueOnce({})
    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          RouterLink: { template: '<a><slot /></a>' }
        }
      }
    })
    
    await wrapper.find('input[type="email"]').setValue('test@example.com')
    await wrapper.find('input[type="password"]').setValue('password123')
    await wrapper.find('form').trigger('submit.prevent')

    expect(mockLogin).toHaveBeenCalledWith('test@example.com', 'password123')
    expect(mockPush).toHaveBeenCalledWith('/')
  })

  it('shows error message on login failure', async () => {
    mockLogin.mockRejectedValueOnce({ response: { data: { error: 'Invalid' } } })
    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          RouterLink: { template: '<a><slot /></a>' }
        }
      }
    })
    
    await wrapper.find('form').trigger('submit.prevent')
    
    // Wait for async update
    await vi.waitFor(() => {
        expect(wrapper.find('.text-red-500').exists()).toBe(true)
    })
    expect(wrapper.find('.text-red-500').text()).toBe('Invalid')
  })
})
