<template>
  <div class="h-full flex flex-col bg-gray-50/30 overflow-hidden">
    <!-- Header -->
    <header class="bg-white border-b border-gray-100 flex items-center justify-between px-8 py-4 shrink-0">
      <div>
        <h1 class="text-2xl font-black text-gray-900 tracking-tight">{{ t('nav.map_view') }}</h1>
        <p class="text-xs text-gray-500 font-medium uppercase tracking-wider mt-0.5">Farm Layout & Health Status</p>
      </div>

      <div class="flex items-center gap-4">
        <!-- View Toggle -->
        <div class="bg-gray-100 p-1 rounded-xl flex gap-1">
          <button 
            @click="viewMode = 'map'"
            class="px-4 py-1.5 rounded-lg text-sm font-bold transition-all"
            :class="viewMode === 'map' ? 'bg-white shadow-sm text-brand-dark' : 'text-gray-500 hover:text-gray-700'"
          >
            Map View
          </button>
          <button 
            @click="viewMode = 'list'"
            class="px-4 py-1.5 rounded-lg text-sm font-bold transition-all"
            :class="viewMode === 'list' ? 'bg-white shadow-sm text-brand-dark' : 'text-gray-500 hover:text-gray-700'"
          >
            List View
          </button>
        </div>

        <div class="h-6 w-px bg-gray-200"></div>

        <!-- Edit Toggle -->
        <button 
          @click="store.toggleEditing"
          class="flex items-center gap-2 px-5 py-2 rounded-xl transition-all font-bold"
          :class="store.isEditingLayout ? 'bg-brand-dark text-white shadow-lg' : 'bg-white border border-gray-200 text-gray-700 hover:border-gray-300 shadow-sm'"
        >
          <span v-if="store.isEditingLayout">💾 Save Layout</span>
          <span v-else>🛠 Edit Layout</span>
        </button>
      </div>
    </header>

    <!-- Main Content Area -->
    <div class="flex-1 overflow-hidden relative flex">
      <!-- Toolbar (Only in Edit Mode) -->
      <transition name="slide-left">
        <aside v-if="store.isEditingLayout" class="w-72 bg-white border-r border-gray-100 shadow-2xl z-10 flex flex-col">
          <div class="p-6 border-b border-gray-50">
            <h3 class="font-black text-gray-900 text-lg">Builder Tools</h3>
            <p class="text-xs text-gray-500 mt-1">Drag and drop fields to arrange your farm layout.</p>
          </div>
          
          <div class="p-6 space-y-6">
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-3">Add Elements</label>
              <div class="grid grid-cols-2 gap-3">
                <button 
                  @click="addNewField"
                  class="flex flex-col items-center gap-2 p-4 rounded-2xl border-2 border-dashed border-gray-100 hover:border-brand-dark hover:bg-green-50/30 transition-all group"
                >
                  <div class="w-10 h-10 border-2 border-brand-dark rounded-lg group-hover:bg-brand-dark group-hover:text-white flex items-center justify-center text-brand-dark transition-all">
                    <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4" /></svg>
                  </div>
                  <span class="text-xs font-bold text-gray-600">Rectangle</span>
                </button>
              </div>
            </div>

            <div v-if="selectedField" class="bg-gray-50 rounded-2xl p-5 border border-gray-100">
              <label class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-4">Field Properties</label>
              <div class="space-y-4">
                <div>
                  <label class="text-xs font-bold text-gray-600 mb-1 block">Field ID</label>
                  <input 
                    v-model="selectedField.id" 
                    class="w-full bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-brand-dark focus:border-brand-dark outline-none font-bold"
                  />
                </div>
                <div>
                  <label class="text-xs font-bold text-gray-600 mb-1 block">Name</label>
                  <input 
                    v-model="selectedField.name" 
                    class="w-full bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-brand-dark focus:border-brand-dark outline-none"
                  />
                </div>
                <button 
                  @click="deleteSelected"
                  class="w-full py-3 text-red-600 font-bold text-[13px] hover:bg-red-50 rounded-xl transition-all"
                >
                  Delete Field
                </button>
              </div>
            </div>
          </div>
        </aside>
      </transition>

      <!-- SVG Canvas -->
      <div 
        class="flex-1 relative overflow-hidden bg-[url('https://grainy-gradients.vercel.app/noise.svg')] bg-repeat"
        ref="canvasContainer"
      >
        <!-- Grid Background (SVG) -->
        <svg class="absolute inset-0 w-full h-full pointer-events-none opacity-[0.03]">
          <defs>
            <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 40" fill="none" stroke="currentColor" stroke-width="1"/>
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid)" />
        </svg>

        <svg 
          viewBox="0 0 1200 800"
          preserveAspectRatio="xMidYMid meet"
          class="w-full h-full"
        >
          <FieldNode
            v-for="field in store.fields"
            :key="field.id"
            :field="field"
            :is-editing="store.isEditingLayout"
            @update="onFieldUpdate"
            @select="onFieldSelect"
          />
        </svg>

        <!-- Legend -->
        <div class="absolute bottom-10 right-10 bg-white/90 backdrop-blur-md rounded-2xl p-4 shadow-xl border border-white/50 flex gap-6">
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-status-optimalDark"></div>
            <span class="text-xs font-bold text-gray-700">Optimal</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-status-warningDark"></div>
            <span class="text-xs font-bold text-gray-700">Warning</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-status-criticalDark"></div>
            <span class="text-xs font-bold text-gray-700">Critical</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from '@/i18n'
import { useFieldStore } from '@/stores/fields.store'
import FieldNode from '@/components/fields/FieldNode.vue'
import type { Field } from '@/types'

const { t } = useI18n()
const store = useFieldStore()
const viewMode = ref<'map' | 'list'>('map')
const selectedId = ref<string | null>(null)

const selectedField = computed(() => {
  return store.fields.find(f => f.id === selectedId.value)
})

onMounted(() => {
  store.fetchFields()
})

function onFieldUpdate(id: string, updates: Partial<Field>) {
  store.updateField(id, updates)
}

function onFieldSelect(id: string) {
  selectedId.value = id
}

function addNewField() {
  const nextNum = store.fields.length + 1
  const id = `F-${String(nextNum).padStart(2, '0')}`
  
  store.saveField({
    id,
    name: 'New Field',
    shape: 'RECTANGLE',
    x: 100,
    y: 100,
    width: 200,
    height: 150
  })
}

function deleteSelected() {
  if (selectedId.value) {
    store.deleteField(selectedId.value)
    selectedId.value = null
  }
}
</script>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}

.slide-left-enter-from,
.slide-left-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}
</style>
