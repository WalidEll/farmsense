<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-green-50 to-emerald-100 p-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
      <div class="text-center mb-8">
        <div class="text-4xl mb-2">🌱</div>
        <h1 class="text-2xl font-bold text-green-800">FarmSense</h1>
        <p class="text-gray-500 text-sm mt-1">{{ t('auth.registerSubtitle') }}</p>
      </div>

      <form @submit.prevent="submit" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('auth.name') }}</label>
          <input
            v-model="name"
            type="text"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('auth.email') }}</label>
          <input
            v-model="email"
            type="email"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('auth.password') }}</label>
          <input
            v-model="password"
            type="password"
            required
            minlength="8"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('auth.whatsapp') }} ({{ t('common.optional') }})</label>
          <input
            v-model="phoneWa"
            type="tel"
            placeholder="+212600000000"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
          />
        </div>

        <!-- Language picker -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">{{ t('settings.language') }}</label>
          <div class="flex gap-2">
            <button
              v-for="l in langs"
              :key="l.value"
              type="button"
              @click="lang = l.value"
              :class="[
                'flex-1 py-2 rounded-lg text-sm font-medium border',
                lang === l.value
                  ? 'bg-green-700 text-white border-green-700'
                  : 'bg-white text-gray-700 border-gray-300 hover:border-green-500',
              ]"
            >
              {{ l.label }}
            </button>
          </div>
        </div>

        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-green-700 hover:bg-green-800 text-white font-semibold py-2.5 rounded-lg disabled:opacity-50"
        >
          {{ loading ? '...' : t('auth.createAccount') }}
        </button>
      </form>

      <p class="text-center text-sm text-gray-500 mt-6">
        {{ t('auth.hasAccount') }}
        <RouterLink to="/login" class="text-green-700 font-medium hover:underline">
          {{ t('auth.login') }}
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
import type { Lang } from '@/types'

const { t } = useI18n()
const auth = useAuthStore()
const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const phoneWa = ref('')
const lang = ref<Lang>('FR')
const error = ref('')
const loading = ref(false)

const langs = [
  { value: 'FR' as Lang, label: 'Français' },
  { value: 'AR' as Lang, label: 'العربية' },
  { value: 'EN' as Lang, label: 'English' },
]

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.register(name.value, email.value, password.value, lang.value, phoneWa.value || undefined)
    router.push('/')
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? t('auth.registerError')
  } finally {
    loading.value = false
  }
}
</script>
