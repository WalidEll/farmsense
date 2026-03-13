<template>
  <g 
    class="field-node group cursor-pointer"
    :class="[statusClass, { 'is-dragging': isDragging }]"
    @mousedown="onMouseDown"
  >
    <!-- Field Shape (Rectangle for now) -->
    <rect
      :x="field.x"
      :y="field.y"
      :width="field.width"
      :height="field.height"
      rx="12"
      class="field-rect transition-all duration-300 fill-current opacity-20 stroke-2"
    />
    
    <!-- Field ID Label -->
    <text
      :x="field.x + field.width / 2"
      :y="field.y + field.height / 2 - 10"
      text-anchor="middle"
      class="text-[12px] font-black uppercase tracking-widest fill-gray-700"
    >
      {{ field.id }}
    </text>
    
    <!-- Status Indicator Dot -->
    <circle
      :cx="field.x + field.width / 2"
      :cy="field.y + field.height / 2 + 15"
      r="4"
      class="status-dot shadow-sm"
    />
    
    <!-- Selection/Hover Border -->
    <rect
      :x="field.x - 2"
      :y="field.y - 2"
      :width="field.width + 4"
      :height="field.height + 4"
      rx="14"
      fill="none"
      class="stroke-brand-dark/0 group-hover:stroke-brand-dark/20 stroke-1 dashed transition-all"
    />
  </g>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import type { Field } from '@/types'

const props = defineProps<{
  field: Field
  isEditing: boolean
}>()

const emit = defineEmits<{
  update: [id: string, updates: Partial<Field>]
  select: [id: string]
}>()

const isDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })
const initialPos = ref({ x: 0, y: 0 })

const statusClass = computed(() => {
  switch (props.field.status) {
    case 'OPTIMAL': return 'text-status-optimalDark stroke-status-optimalDark'
    case 'WARNING': return 'text-status-warningDark stroke-status-warningDark'
    case 'CRITICAL': return 'text-status-criticalDark stroke-status-criticalDark'
    default: return 'text-gray-400 stroke-gray-400'
  }
})

function onMouseDown(e: MouseEvent) {
  if (!props.isEditing) {
    emit('select', props.field.id)
    return
  }
  
  isDragging.value = true
  dragStart.value = { x: e.clientX, y: e.clientY }
  initialPos.value = { x: props.field.x, y: props.field.y }
  
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

function onMouseMove(e: MouseEvent) {
  if (!isDragging.value) return
  
  const dx = e.clientX - dragStart.value.x
  const dy = e.clientY - dragStart.value.y
  
  // Snap to grid (10px)
  const newX = Math.round((initialPos.value.x + dx) / 10) * 10
  const newY = Math.round((initialPos.value.y + dy) / 10) * 10
  
  emit('update', props.field.id, { x: newX, y: newY })
}

function onMouseUp() {
  isDragging.value = false
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('mouseup', onMouseUp)
}
</script>

<style scoped>
.field-rect {
  pointer-events: all;
}

.status-dot {
  @apply fill-current;
}

.field-node.is-dragging {
  @apply opacity-50;
  cursor: grabbing;
}

.field-node:hover .field-rect {
  @apply opacity-30;
}
</style>
