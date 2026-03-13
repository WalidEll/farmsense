<template>
  <div :dir="auth.isRtl ? 'rtl' : 'ltr'" class="min-h-screen bg-brand-light text-gray-900">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { setGlobalLang } from '@/i18n'
import type { Lang } from '@/i18n'

const auth = useAuthStore()
// Keep the i18n module in sync with auth lang (immediate so first render is correct)
watch(() => auth.lang, (l) => setGlobalLang(l as Lang), { immediate: true })
</script>
