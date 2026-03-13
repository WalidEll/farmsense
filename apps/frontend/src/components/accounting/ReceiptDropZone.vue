<template>
  <div
    @dragover.prevent="isDragging = true"
    @dragleave.prevent="isDragging = false"
    @drop.prevent="onDrop"
    :class="[
      isDragging ? 'border-emerald-400 bg-emerald-50/60 scale-[1.01]' : 'border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50/30',
      'relative border-2 border-dashed rounded-2xl p-10 text-center transition-all duration-300 cursor-pointer group'
    ]"
    @click="($refs.fileInput as HTMLInputElement)?.click()"
  >
    <input
      type="file"
      ref="fileInput"
      class="hidden"
      accept="image/*"
      @change="onFileSelect"
    />

    <!-- Animated scan line when dragging -->
    <div v-if="isDragging" class="absolute inset-x-4 top-0 h-0.5 bg-gradient-to-r from-transparent via-emerald-400 to-transparent animate-pulse rounded-full"></div>

    <div class="flex flex-col items-center gap-4">
      <div :class="[
        isDragging ? 'bg-emerald-100 text-emerald-500 shadow-emerald-200/50 shadow-lg' : 'bg-gray-100 text-gray-400 group-hover:bg-emerald-50 group-hover:text-emerald-500',
        'p-4 rounded-2xl transition-all duration-300'
      ]">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6.827 6.175A2.31 2.31 0 015.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 00-1.134-.175 2.31 2.31 0 01-1.64-1.055l-.822-1.316a2.192 2.192 0 00-1.736-1.039 48.774 48.774 0 00-5.232 0 2.192 2.192 0 00-1.736 1.039l-.821 1.316z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 12.75a4.5 4.5 0 11-9 0 4.5 4.5 0 019 0zM18.75 10.5h.008v.008h-.008V10.5z" />
        </svg>
      </div>

      <div>
        <h3 :class="[isDragging ? 'text-emerald-700' : 'text-gray-800', 'text-base font-bold transition-colors']">
          {{ isDragging ? (t('receipt_drop_here') || 'Deposez ici') : t('receipt_upload') }}
        </h3>
        <p class="text-sm text-gray-400 mt-1.5">
          {{ t('receipt_upload_hint') || 'Glissez une photo de recu ou cliquez pour parcourir' }}
        </p>
        <p class="text-[11px] text-gray-300 mt-2 font-medium">JPEG, PNG, WebP &middot; Max 10 MB</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from '@/i18n'

const { t } = useI18n()
const isDragging = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

const emit = defineEmits<{
  (e: 'upload', file: File): void
}>()

const onDrop = (e: DragEvent) => {
  isDragging.value = false
  const file = e.dataTransfer?.files[0]
  if (file && file.type.startsWith('image/')) {
    emit('upload', file)
  }
}

const onFileSelect = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file) {
    emit('upload', file)
  }
}
</script>
