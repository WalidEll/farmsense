<template>
  <div
    v-if="!online"
    class="bg-amber-400 text-amber-900 text-sm font-medium text-center py-2 px-4"
  >
    📵 {{ t('offline.banner') }}
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from '@/i18n'

const { t } = useI18n()
const online = ref(navigator.onLine)

function update() { online.value = navigator.onLine }

onMounted(() => {
  window.addEventListener('online', update)
  window.addEventListener('offline', update)
})
onUnmounted(() => {
  window.removeEventListener('online', update)
  window.removeEventListener('offline', update)
})
</script>
