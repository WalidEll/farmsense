<template>
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b">
        <h3 class="text-lg font-bold text-gray-800">Connect Sensor</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-2xl leading-none">×</button>
      </div>

      <div class="p-6 space-y-5">
        <!-- Loading state -->
        <div v-if="loading" class="text-center py-8 text-gray-400">
          <div class="text-3xl mb-2">📡</div>
          <p class="text-sm">Generating setup code...</p>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="text-center py-8">
          <div class="text-3xl mb-2">⚠️</div>
          <p class="text-sm text-red-500 mb-4">{{ error }}</p>
          <button @click="fetchCode" class="text-sm text-green-700 underline">Try again</button>
        </div>

        <!-- Success state -->
        <template v-else-if="code">
          <!-- Claim code display -->
          <div class="text-center">
            <p class="text-xs text-gray-500 uppercase tracking-wide mb-2">Your Setup Code</p>
            <div class="inline-block bg-green-50 border-2 border-green-600 rounded-xl px-8 py-4">
              <span class="text-4xl font-mono font-bold text-green-700 tracking-widest">{{ code }}</span>
            </div>
            <p class="text-xs text-gray-400 mt-2">
              Expires at {{ expiresLabel }}
            </p>
          </div>

          <!-- Step-by-step instructions -->
          <ol class="space-y-3 text-sm">
            <li class="flex gap-3">
              <span class="flex-shrink-0 w-6 h-6 rounded-full bg-green-700 text-white text-xs flex items-center justify-center font-bold">1</span>
              <span class="text-gray-700">Power on your FarmSense sensor. The LED will blink 3 times.</span>
            </li>
            <li class="flex gap-3">
              <span class="flex-shrink-0 w-6 h-6 rounded-full bg-green-700 text-white text-xs flex items-center justify-center font-bold">2</span>
              <span class="text-gray-700">
                On your phone or laptop, connect to the WiFi network
                <code class="bg-gray-100 px-1 rounded font-mono text-xs">FarmSense-Setup</code>.
              </span>
            </li>
            <li class="flex gap-3">
              <span class="flex-shrink-0 w-6 h-6 rounded-full bg-green-700 text-white text-xs flex items-center justify-center font-bold">3</span>
              <span class="text-gray-700">
                A configuration page opens automatically (or go to
                <code class="bg-gray-100 px-1 rounded font-mono text-xs">192.168.4.1</code>).
              </span>
            </li>
            <li class="flex gap-3">
              <span class="flex-shrink-0 w-6 h-6 rounded-full bg-green-700 text-white text-xs flex items-center justify-center font-bold">4</span>
              <div class="text-gray-700">
                Fill in the form:
                <ul class="mt-1 space-y-0.5 text-gray-500 text-xs">
                  <li>• WiFi SSID + Password (your home network)</li>
                  <li>• Setup Code: <strong class="text-green-700 font-mono">{{ code }}</strong></li>
                  <li>• API Host: <code class="bg-gray-100 px-1 rounded font-mono">{{ apiHost }}</code></li>
                </ul>
              </div>
            </li>
            <li class="flex gap-3">
              <span class="flex-shrink-0 w-6 h-6 rounded-full bg-green-700 text-white text-xs flex items-center justify-center font-bold">5</span>
              <span class="text-gray-700">Click Save. The sensor will connect and appear in your dashboard within a minute.</span>
            </li>
          </ol>
        </template>
      </div>

      <div class="px-6 pb-6">
        <button
          @click="$emit('close')"
          class="w-full border border-gray-300 text-gray-700 rounded-lg py-2.5 hover:bg-gray-50 text-sm"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { api } from '@/services/api'

const emit = defineEmits<{ close: [] }>()

const loading = ref(true)
const error = ref('')
const code = ref('')
const expiresAt = ref<string>('')

const apiHost = import.meta.env.VITE_API_URL
  ? new URL(import.meta.env.VITE_API_URL).origin
  : 'http://192.168.11.109:8080'

const expiresLabel = computed(() => {
  if (!expiresAt.value) return ''
  return new Date(expiresAt.value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
})

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
