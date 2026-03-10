<template>
  <div class="flex flex-col items-center justify-center h-[60vh] text-center p-8">
    <div class="bg-green-50 p-6 rounded-full mb-6 animate-bounce">
      <span class="text-6xl">🚧</span>
    </div>
    <h1 class="text-3xl font-bold text-gray-800 mb-2">{{ t('common.coming_soon_title') }}</h1>
    <p class="text-gray-500 max-w-md">
      {{ t('common.coming_soon_subtitle', { name: routeName }) }}
    </p>
    <button
      @click="$router.push('/')"
      class="mt-8 px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
    >
      {{ t('common.coming_soon_return') }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from '@/i18n'

const { t } = useI18n()
const route = useRoute()

const routeName = computed(() => {
  const path = route.name as string || route.path.split('/').pop() || 'Feature'
  return path.charAt(0).toUpperCase() + path.slice(1) + ' View'
})
</script>
