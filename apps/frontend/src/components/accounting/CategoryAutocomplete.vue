<template>
  <div class="relative">
    <label v-if="label" class="block text-sm font-medium text-gray-700 mb-1">{{ label }}</label>
    <div class="relative">
      <input
        type="text"
        v-model="query"
        @input="onInput"
        @focus="isOpen = true"
        class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-green-500 focus:ring-green-500 sm:text-sm"
        :placeholder="placeholder"
      />
      
      <div v-if="isOpen && filteredCategories.length > 0" class="absolute z-10 mt-1 w-full bg-white shadow-lg max-height-60 rounded-md py-1 text-base ring-1 ring-black ring-opacity-5 overflow-auto focus:outline-none sm:text-sm">
        <div
          v-for="category in filteredCategories"
          :key="category"
          @click="selectCategory(category)"
          class="cursor-pointer select-none relative py-2 pl-3 pr-9 hover:bg-green-50 text-gray-900"
        >
          {{ category }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  modelValue: string
  categories: string[]
  label?: string
  placeholder?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const query = ref(props.modelValue)
const isOpen = ref(false)

const filteredCategories = computed(() => {
  if (!query.value) return props.categories
  return props.categories.filter(c => 
    c.toLowerCase().includes(query.value.toLowerCase())
  )
})

const onInput = () => {
  emit('update:modelValue', query.value)
  isOpen.value = true
}

const selectCategory = (category: string) => {
  query.value = category
  emit('update:modelValue', category)
  isOpen.value = false
}

const closeOnOutsideClick = (e: MouseEvent) => {
  if (!(e.target as HTMLElement).closest('.relative')) {
    isOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', closeOnOutsideClick))
onUnmounted(() => document.removeEventListener('click', closeOnOutsideClick))
</script>
