<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-green-50 to-emerald-100 p-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
      <!-- Logo -->
      <div class="text-center mb-8">
        <div class="text-4xl mb-2">🌱</div>
        <h1 class="text-2xl font-bold text-green-800">FarmSense</h1>
        <p class="text-gray-500 text-sm mt-1">{{ t('auth.loginSubtitle') }}</p>
      </div>

      <form @submit.prevent="submit" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('auth.email') }}</label>
          <input
            v-model="email"
            type="email"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
            :placeholder="t('auth.email')"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('auth.password') }}</label>
          <input
            v-model="password"
            type="password"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
            :placeholder="t('auth.password')"
          />
        </div>

        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-green-700 hover:bg-green-800 text-white font-semibold py-2.5 rounded-lg disabled:opacity-50"
        >
          {{ loading ? '...' : t('auth.login') }}
        </button>
      </form>

      <p class="text-center text-sm text-gray-500 mt-6">
        {{ t('auth.noAccount') }}
        <RouterLink to="/register" class="text-green-700 font-medium hover:underline">
          {{ t('auth.register') }}
        </RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useI18n } from '@/i18n'

const { t } = useI18n()
const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(email.value, password.value)
    router.push('/')
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? t('auth.loginError')
  } finally {
    loading.value = false
  }
}
</script>
