<template>
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md max-h-[90vh] flex flex-col">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b shrink-0">
        <h3 class="text-lg font-bold text-gray-800">{{ t('dashboard.connectSensor') }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-2xl leading-none">&times;</button>
      </div>

      <div class="p-6 space-y-5 overflow-y-auto">
        <!-- Loading state -->
        <div v-if="loading" class="text-center py-8 text-gray-400">
          <div class="text-3xl mb-2 animate-pulse">📡</div>
          <p class="text-sm">{{ t('common.loading') }}</p>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="text-center py-8">
          <div class="text-3xl mb-2">⚠️</div>
          <p class="text-sm text-red-500 mb-4">{{ error }}</p>
          <button @click="fetchCode" class="text-sm text-green-700 underline font-medium">
            {{ t('common.retry') }}
          </button>
        </div>

        <!-- Success state -->
        <template v-else-if="code">
          <!-- Setup code display -->
          <div class="text-center">
            <p class="text-xs text-gray-500 uppercase tracking-wide mb-2 font-medium">
              {{ t('sensor.setup_title') }}
            </p>
            <div
              class="relative inline-block bg-green-50 border-2 border-green-600 rounded-xl px-8 py-5 cursor-pointer group"
              @click="copyCode"
            >
              <span class="text-5xl font-mono font-bold text-green-700 tracking-[0.3em] select-all">{{ code }}</span>
              <!-- Copy button -->
              <button
                class="absolute -top-2 -end-2 bg-white border border-gray-200 rounded-lg p-1.5 shadow-sm opacity-0 group-hover:opacity-100 transition-opacity"
                :title="copied ? 'Copied!' : 'Copy code'"
              >
                <svg v-if="!copied" class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                </svg>
                <svg v-else class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
              </button>
            </div>
            <p class="text-xs text-gray-400 mt-2">
              {{ t('sensor.last_updated') }} {{ expiresLabel }}
            </p>
            <p v-if="copied" class="text-xs text-green-600 mt-1 font-medium">
              Copied!
            </p>
          </div>

          <!-- Visual step indicator -->
          <div class="space-y-0">
            <div v-for="(step, idx) in steps" :key="idx" class="flex gap-4">
              <!-- Step circle + line -->
              <div class="flex flex-col items-center">
                <div class="w-8 h-8 rounded-full bg-green-700 text-white text-xs flex items-center justify-center font-bold shrink-0">
                  {{ idx + 1 }}
                </div>
                <div v-if="idx < steps.length - 1" class="w-0.5 flex-1 bg-green-200 my-1 min-h-[16px]"></div>
              </div>
              <!-- Step content -->
              <div class="pb-4" :class="{ 'pb-0': idx === steps.length - 1 }">
                <p class="text-sm font-medium text-gray-800 mb-0.5">{{ step.title }}</p>
                <p class="text-xs text-gray-500">{{ step.description }}</p>
                <!-- Extra content for step 4 (form fields) -->
                <div v-if="idx === 3" class="mt-2 bg-gray-50 rounded-lg p-3 space-y-1.5">
                  <div class="flex items-center gap-2 text-xs text-gray-600">
                    <span class="w-1.5 h-1.5 bg-green-500 rounded-full shrink-0"></span>
                    WiFi SSID + Password
                  </div>
                  <div class="flex items-center gap-2 text-xs">
                    <span class="w-1.5 h-1.5 bg-green-500 rounded-full shrink-0"></span>
                    <span class="text-gray-600">Setup Code: </span>
                    <code class="text-green-700 font-mono font-bold">{{ code }}</code>
                  </div>
                  <div class="flex items-center gap-2 text-xs">
                    <span class="w-1.5 h-1.5 bg-green-500 rounded-full shrink-0"></span>
                    <span class="text-gray-600">API Host: </span>
                    <code class="text-gray-700 font-mono bg-white px-1.5 py-0.5 rounded border border-gray-200 text-[11px]">{{ apiHost }}</code>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- WiFi Tips -->
          <div class="bg-amber-50 border border-amber-200 rounded-xl p-4">
            <p class="text-xs font-semibold text-amber-800 mb-2 flex items-center gap-1.5">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              WiFi Tips
            </p>
            <ul class="space-y-1 text-xs text-amber-700">
              <li>- Make sure the sensor is within range of your WiFi router</li>
              <li>- Use a 2.4GHz network (ESP32 does not support 5GHz)</li>
              <li>- If the captive portal does not appear, open <code class="bg-white/60 px-1 rounded font-mono">192.168.4.1</code> in your browser</li>
              <li>- After setup, the sensor LED stays solid green when connected</li>
            </ul>
          </div>
        </template>
      </div>

      <div class="px-6 pb-6 shrink-0">
        <button
          @click="$emit('close')"
          class="w-full border border-gray-300 text-gray-700 rounded-lg py-2.5 hover:bg-gray-50 text-sm font-medium"
        >
          {{ t('common.cancel') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { api } from '@/services/api'
import { useI18n } from '@/i18n'

const { t } = useI18n()
const emit = defineEmits<{ close: [] }>()

const loading = ref(true)
const error = ref('')
const code = ref('')
const expiresAt = ref<string>('')
const copied = ref(false)

const apiHost = import.meta.env.VITE_API_URL
  ? new URL(import.meta.env.VITE_API_URL).origin
  : 'http://192.168.11.109:8080'

const expiresLabel = computed(() => {
  if (!expiresAt.value) return ''
  return new Date(expiresAt.value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
})

const steps = [
  {
    title: 'Power on your sensor',
    description: 'Plug in the FarmSense ESP32 sensor. The LED will blink 3 times to confirm it is ready.',
  },
  {
    title: 'Connect to FarmSense-Setup WiFi',
    description: 'On your phone or laptop, open WiFi settings and connect to the "FarmSense-Setup" network.',
  },
  {
    title: 'Open the configuration page',
    description: 'A captive portal should open automatically. If not, navigate to 192.168.4.1 in your browser.',
  },
  {
    title: 'Fill in the setup form',
    description: 'Enter the following information in the configuration page:',
  },
  {
    title: 'Save and connect',
    description: 'Click Save on the portal. The sensor will reboot and connect to your dashboard within a minute.',
  },
]

async function copyCode() {
  try {
    await navigator.clipboard.writeText(code.value)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  } catch {
    // Fallback: select the text
  }
}

async function fetchCode() {
  loading.value = true
  error.value = ''
  code.value = ''
  try {
    const res = await api.post<{ code: string; expiresAt: string }>('/devices/setup-code')
    code.value = res.code
    expiresAt.value = res.expiresAt
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to generate setup code.'
  } finally {
    loading.value = false
  }
}

onMounted(fetchCode)
</script>
