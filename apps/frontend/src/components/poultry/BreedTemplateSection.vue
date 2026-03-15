<template>
  <section class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
    <button
      class="w-full px-6 py-4 flex items-center justify-between border-b border-gray-50 hover:bg-gray-50/50 transition-colors"
      @click="isOpen = !isOpen"
    >
      <h2 class="font-bold text-gray-900 flex items-center gap-2">
        <span class="text-xl">{{ icon }}</span>
        {{ title }}
      </h2>
      <span
        class="text-gray-400 transition-transform duration-200"
        :class="{ 'rotate-180': isOpen }"
      >▼</span>
    </button>
    <div
      v-show="isOpen"
      class="transition-all duration-200 ease-in-out"
    >
      <slot />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  title: string
  icon: string
}>()

const isOpen = ref(true)
</script>
