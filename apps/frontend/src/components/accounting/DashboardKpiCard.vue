<template>
  <div :class="[cardClass, 'group relative overflow-hidden rounded-2xl p-5 transition-all duration-300 hover:shadow-lg hover:-translate-y-0.5 cursor-default']">
    <!-- Decorative background circle -->
    <div :class="[decorBgClass, 'absolute -top-6 -right-6 w-24 h-24 rounded-full opacity-[0.07] transition-transform duration-500 group-hover:scale-150']"></div>

    <div class="relative flex items-start justify-between">
      <div class="space-y-2">
        <p class="text-xs font-semibold uppercase tracking-widest opacity-60">{{ label }}</p>
        <p class="text-2xl font-extrabold tracking-tight leading-none">
          {{ formattedValue }}
        </p>
        <p v-if="unit" class="text-xs font-medium opacity-50">{{ unit }}</p>
      </div>
      <div :class="[iconWrapClass, 'flex-shrink-0 p-2.5 rounded-xl shadow-sm']">
        <component :is="icon" class="w-5 h-5" />
      </div>
    </div>

    <!-- Subtle bottom accent line -->
    <div :class="[accentClass, 'absolute bottom-0 left-0 right-0 h-[3px] opacity-40']"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  label: string
  value: number
  unit?: string
  icon: any
  color?: 'green' | 'red' | 'blue' | 'yellow' | 'purple'
  isCurrency?: boolean
}>()

const cardClass = computed(() => {
  switch (props.color) {
    case 'green': return 'bg-gradient-to-br from-emerald-50 to-green-50 text-emerald-900 border border-emerald-100/60'
    case 'red': return 'bg-gradient-to-br from-rose-50 to-red-50 text-rose-900 border border-rose-100/60'
    case 'blue': return 'bg-gradient-to-br from-sky-50 to-blue-50 text-sky-900 border border-sky-100/60'
    case 'yellow': return 'bg-gradient-to-br from-amber-50 to-yellow-50 text-amber-900 border border-amber-100/60'
    case 'purple': return 'bg-gradient-to-br from-violet-50 to-purple-50 text-violet-900 border border-violet-100/60'
    default: return 'bg-gradient-to-br from-gray-50 to-slate-50 text-gray-900 border border-gray-100/60'
  }
})

const decorBgClass = computed(() => {
  switch (props.color) {
    case 'green': return 'bg-emerald-500'
    case 'red': return 'bg-rose-500'
    case 'blue': return 'bg-sky-500'
    case 'yellow': return 'bg-amber-500'
    case 'purple': return 'bg-violet-500'
    default: return 'bg-gray-500'
  }
})

const iconWrapClass = computed(() => {
  switch (props.color) {
    case 'green': return 'bg-emerald-100 text-emerald-600'
    case 'red': return 'bg-rose-100 text-rose-600'
    case 'blue': return 'bg-sky-100 text-sky-600'
    case 'yellow': return 'bg-amber-100 text-amber-600'
    case 'purple': return 'bg-violet-100 text-violet-600'
    default: return 'bg-gray-100 text-gray-600'
  }
})

const accentClass = computed(() => {
  switch (props.color) {
    case 'green': return 'bg-emerald-500'
    case 'red': return 'bg-rose-500'
    case 'blue': return 'bg-sky-500'
    case 'yellow': return 'bg-amber-500'
    case 'purple': return 'bg-violet-500'
    default: return 'bg-gray-400'
  }
})

const formattedValue = computed(() => {
  if (props.isCurrency) {
    return new Intl.NumberFormat('fr-MA', {
      style: 'currency',
      currency: 'MAD',
      maximumFractionDigits: 0
    }).format(props.value)
  }
  return props.value.toLocaleString()
})
</script>
